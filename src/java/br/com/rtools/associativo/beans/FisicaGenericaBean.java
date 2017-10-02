package br.com.rtools.associativo.beans;

import br.com.rtools.arrecadacao.dao.OposicaoDao;
import br.com.rtools.associativo.Socios;
import br.com.rtools.associativo.dao.SociosDao;
import br.com.rtools.endereco.Cidade;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.TipoDocumento;
import br.com.rtools.pessoa.dao.FisicaDao;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.Rotina;
import static br.com.rtools.seguranca.Usuario.getUsuario;
import br.com.rtools.seguranca.controleUsuario.ChamadaPaginaBean;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import static br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean.getCliente;
import br.com.rtools.utilitarios.AnaliseString;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.PF;
import br.com.rtools.utilitarios.ValidaDocumentos;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import org.apache.commons.io.FileUtils;
import org.primefaces.context.RequestContext;

@ManagedBean
@SessionScoped
public class FisicaGenericaBean implements Serializable {

    private Fisica fisica;
    private Fisica fisicaPesquisa;
    private Boolean visibleModal;
    private List<Fisica> listFisicaSugestao;
    private List<Fisica> listFisicaSugestao2;
    private List<Socios> listSubSocios;

    public FisicaGenericaBean() {
        fisica = new Fisica();
        fisica.setEstadoCivil("Solteiro(a)");
        fisica.setNacionalidade("Brasileiro(a)");
        String nat;
        Cidade c = Registro.get().getFilial().getPessoa().getPessoaEndereco().getEndereco().getCidade();
        nat = c.getCidade();
        nat = nat + " - " + c.getUf();
        fisica.setNaturalidade(nat);
        visibleModal = false;
        listFisicaSugestao = new ArrayList();
        listFisicaSugestao2 = new ArrayList();
        listSubSocios = new ArrayList();
    }

    public void saveFisica() {
        if (!validationDependente()) {
            return;
        }
        Boolean showMessage = true;
        Rotina r = new Rotina().get();
        if (r.getId() == 469) {
            // showMessage = false;
        }
        Dao dao = new Dao();

        fisica.getPessoa().setNome(fisica.getPessoa().getNome().trim());

        if (fisica.getId() == -1) {
            fisica.getPessoa().setTipoDocumento((TipoDocumento) dao.find(new TipoDocumento(), 1));
            dao.openTransaction();
            if (!dao.save(fisica.getPessoa())) {
                GenericaMensagem.error("Erro", "Erro ao salvar Pessoa!");
                dao.rollback();
                return;
            }

            if (!dao.save(fisica)) {
                GenericaMensagem.error("Erro", "Erro ao salvar Cadastro!");
                dao.rollback();
                return;
            }
            dao.commit();
            NovoLog novoLog = new NovoLog();
            String saveString = "ID: " + fisica.getId()
                    + " - Pessoa (ID: " + fisica.getPessoa().getId() + " - Nome: " + fisica.getPessoa().getNome() + " - Documento: " + fisica.getPessoa().getDocumento() + ")"
                    + " - RG: " + fisica.getRg()
                    + " - Nascimento: " + fisica.getNascimento()
                    + " - Estado Cívil: " + fisica.getEstadoCivil();
            novoLog.save(saveString);
            if (showMessage) {
                GenericaMensagem.info("Sucesso", "Registro inserido!");
            }
        } else {
            dao.openTransaction();
            if (!dao.update(fisica.getPessoa())) {
                GenericaMensagem.error("Erro", "Erro ao atualizar Pessoa!");
                dao.rollback();
                return;
            }

            if (!dao.update(fisica)) {
                GenericaMensagem.error("Erro", "Erro ao atualizar Cadastro!");
                dao.rollback();
                return;
            }
            Fisica f = (Fisica) dao.find(fisica);
            dao.commit();
            if (showMessage) {
                GenericaMensagem.info("Sucesso", "Registro atualizado!");
            }
            NovoLog novoLog = new NovoLog();
            String saveString = "ID: " + fisica.getId()
                    + " - Pessoa (ID: " + fisica.getPessoa().getId() + " - Nome: " + fisica.getPessoa().getNome() + " - Documento: " + fisica.getPessoa().getDocumento() + ")"
                    + " - RG: " + fisica.getRg()
                    + " - Nascimento: " + fisica.getNascimento()
                    + " - Estado Cívil: " + fisica.getEstadoCivil();
            String beforeUpdate = "ID: " + f.getId()
                    + " - Pessoa (ID: " + f.getPessoa().getId() + " - Nome: " + f.getPessoa().getNome() + " - Documento: " + f.getPessoa().getDocumento() + ")"
                    + " - RG: " + f.getRg()
                    + " - Nascimento: " + f.getNascimento()
                    + " - Estado Cívil: " + f.getEstadoCivil();
            novoLog.update(beforeUpdate, saveString);
        }
        closeModal();
        PF.update("form_pessoa_fisica_generica");
        GenericaSessao.put("fisicaPesquisaGenerica", fisica);
        if (r.getId() == 469) {
            GenericaSessao.put("pesquisaFisicaTipo", "dependente");
            PF.update("form_campeonato_equipe");
        }
        fisica = new Fisica();
    }

    public void openModal() {
        fisica = new Fisica();
        fisica.setEstadoCivil("Solteiro(a)");
        visibleModal = true;
        PF.update("form_pessoa_fisica_generica");
        listFisicaSugestao = new ArrayList();
        listFisicaSugestao2 = new ArrayList();
        fisicaPesquisa = null;
    }

    public void closeModal() {
        visibleModal = false;
    }

    public Fisica getFisica() {
        if (GenericaSessao.exists("cidadePesquisa")) {
            String nat;
            Cidade cidade;
            if (GenericaSessao.exists("cidadePesquisa")) {
                cidade = (Cidade) GenericaSessao.getObject("cidadePesquisa", true);
                nat = cidade.getCidade();
                nat = nat + " - " + cidade.getUf();
                fisica.setNaturalidade(nat);
            }
        }
        return fisica;
    }

    public void setFisica(Fisica fisica) {
        this.fisica = fisica;
    }

    public Boolean getVisibleModal() {
        return visibleModal;
    }

    public void setVisibleModal(Boolean visibleModal) {
        this.visibleModal = visibleModal;
    }

    public boolean validationDependente() {
        FisicaDao db = new FisicaDao();
        if (fisica.getId() == -1) {
            if (!db.pesquisaFisicaPorNomeNascRG(fisica.getPessoa().getNome().trim(),
                    fisica.getDtNascimento(),
                    fisica.getRg()).isEmpty()) {
                GenericaMensagem.error("Erro", "Esta pessoa já esta Cadastrada!");
                return false;
            }

            if (fisica.getPessoa().getDocumento().isEmpty() || fisica.getPessoa().getDocumento().equals("0")) {
                fisica.getPessoa().setDocumento("0");
            } else {
                List listDocumento = db.pesquisaFisicaPorDoc(fisica.getPessoa().getDocumento());
                if (!listDocumento.isEmpty()) {
                    GenericaMensagem.error("Erro", "Documento já existente!");
                    return false;
                }
            }
        } else if (fisica.getPessoa().getDocumento().isEmpty() || fisica.getPessoa().getDocumento().equals("0")) {
            fisica.getPessoa().setDocumento("0");
        } else {

            List listDocumento = db.pesquisaFisicaPorDoc(fisica.getPessoa().getDocumento());
            for (Object listDocumento1 : listDocumento) {
                if (!listDocumento.isEmpty() && ((Fisica) listDocumento1).getId() != fisica.getId()) {
                    GenericaMensagem.error("Erro", "Documento já existente!");
                    return false;
                }
            }
        }

        if (fisica.getPessoa().getCriacao().isEmpty()) {
            GenericaMensagem.warn("Atenção", "Data de Cadastro inválida!");
            return false;
        }

        if (fisica.getNascimento().isEmpty() || fisica.getNascimento().length() < 10) {
            GenericaMensagem.fatal("Validação", "Data de Nascimento inválida!");
            return false;
        }

        if (fisica.getPessoa().getNome().equals("")) {
            GenericaMensagem.error("Validação", "O campo nome não pode ser nulo!");
            return false;
        }

        if (!fisica.getPessoa().getDocumento().isEmpty() && !fisica.getPessoa().getDocumento().equals("0")) {
            if (!ValidaDocumentos.isValidoCPF(AnaliseString.extrairNumeros(fisica.getPessoa().getDocumento()))) {
                GenericaMensagem.error("Validação", "Documento Inválido!");
                return false;
            }
        }
        return true;
    }

    public String deleteImage() {
        boolean sucesso = false;

        String fcaminho = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("") + "resources/cliente/" + ControleUsuarioBean.getCliente() + "/imagens/pessoa/" + fisica.getPessoa().getId() + "/" + fisica.getFoto();
        if (new File((fcaminho + ".png")).exists() && FileUtils.deleteQuietly(new File(fcaminho + ".png"))) {
            sucesso = true;
        } else if (new File((fcaminho + ".jpg")).exists() && FileUtils.deleteQuietly(new File(fcaminho + ".jpg"))) {
            sucesso = true;
        } else if (new File((fcaminho + ".jpeg")).exists() && FileUtils.deleteQuietly(new File(fcaminho + ".jpeg"))) {
            sucesso = true;
        } else if (new File((fcaminho + ".gif")).exists() && FileUtils.deleteQuietly(new File(fcaminho + ".gif"))) {
            sucesso = true;
        }

        if (sucesso && fisica.getId() != -1) {
            fisica.setDtFoto(null);
            fisica.setFoto("");
            new Dao().update(fisica, true);
        }

        return null;
    }

    public void existePessoaNomeNascimento() {
        if (fisica.getId() == -1) {
            Fisica f = null;
            if (!fisica.getNascimento().isEmpty() && !fisica.getPessoa().getNome().isEmpty()) {
                FisicaDao db = new FisicaDao();
                f = db.pesquisaFisicaPorNomeNascimento(fisica.getPessoa().getNome(), fisica.getDtNascimento());
                if (f != null) {
                    RequestContext.getCurrentInstance().update("form_pessoa_fisica_generica");
                }
            }
            if (f == null || f.getId() == -1) {
                if (fisica.getId() == -1) {
                    if (!fisica.getPessoa().getNome().isEmpty()) {
                        listFisicaSugestao = new ArrayList();
                        listFisicaSugestao = new FisicaDao().findByNome(fisica.getPessoa().getNome());
                        if (!listFisicaSugestao.isEmpty()) {
                            PF.openDialog("dlg_sugestoes_fisica_generica");
                            PF.update("form_pessoa_fisica_generica:i_sugestoes");
                        }
                    }
                }
            }
        }
    }

    public void useFisicaSugestao(Fisica f) {
        GenericaSessao.put("fisicaPesquisaGenerica", f);
        Rotina r = new Rotina().get();
        if (r.getId() == 469) {
            GenericaSessao.put("pesquisaFisicaTipo", "dependente");
            PF.update("form_campeonato_equipe");
        }
        closeModal();
    }

    public void listenerSubSocios(Integer idPessoa) {
        listSubSocios.clear();
        SociosDao sociosDao = new SociosDao();
        Socios s = sociosDao.pesquisaSocioPorPessoaAtivo(idPessoa);
        if (s != null && s.getId() != -1) {
            listSubSocios = sociosDao.pesquisaDependentePorMatricula(s.getMatriculaSocios().getId(), false);
        }
    }

    public List<Fisica> getListFisicaSugestao() {
        return listFisicaSugestao;
    }

    public void setListFisicaSugestao(List<Fisica> listFisicaSugestao) {
        this.listFisicaSugestao = listFisicaSugestao;
    }

    public void findCPF() {
        if (fisica.getId() == -1) {
            if (!fisica.getPessoa().getDocumento().isEmpty() && !fisica.getPessoa().getDocumento().equals("___.___.___-__")) {
                FisicaDao db = new FisicaDao();
                List<Fisica> listDocumento = db.pesquisaFisicaPorDoc(fisica.getPessoa().getDocumento());
                if (!listDocumento.isEmpty()) {
                    fisica = listDocumento.get(0);
                } else if (fisica.getId() != -1) {
                    String doc = fisica.getPessoa().getDocumento();

                    fisica = new Fisica();
                    fisica.setEstadoCivil("Solteiro(a)");

                    fisica.getPessoa().setDocumento(doc);
                }
            }
        }
    }

    public List<Fisica> listaPesquisaFisica(String query) {
        if (query.isEmpty()) {
            listFisicaSugestao2.clear();
            return null;
        }
        query = query.trim();
        FisicaDao db = new FisicaDao();
        String como = "P";
        if (query.length() <= 2) {
            como = "I";
        }
        String in = "";
        if (new Rotina().get().getId() == 1) {
            in = "";
//            for (int i = 0; i < listDependentes.size(); i++) {
//                in += "," + listDependentes.get(i).getFisica().getPessoa().getId();
//            }
        }
        db.setNot_in(in);
        db.setLimit(1000);
        db.setIgnore(true);
        listFisicaSugestao2 = db.pesquisaPessoa(query, "nome", como);
        if (listFisicaSugestao2.isEmpty()) {
            if (ValidaDocumentos.isValidoCPF(AnaliseString.extrairNumeros(query))) {
                db.setLimit(1);
                db.setIgnore(true);
                listFisicaSugestao2 = db.pesquisaPessoa(query, "cpf", "");
            } else if (DataHoje.isDataValida(query)) {
                db.setIgnore(true);
                db.setLimit(100);
                listFisicaSugestao2 = db.pesquisaPessoa(query, "nascimento", "");
            }
            if (listFisicaSugestao2.isEmpty()) {
                db.setIgnore(true);
                db.setLimit(2);
                listFisicaSugestao2 = db.pesquisaPessoa(query, "rg", "");
            }
        }
        return listFisicaSugestao2;
    }

    public List<Fisica> getListFisicaSugestao2() {
        return listFisicaSugestao2;
    }

    public void setListFisicaSugestao2(List<Fisica> listFisicaSugestao2) {
        this.listFisicaSugestao2 = listFisicaSugestao2;
    }

    public void selectItem() {
        if (fisicaPesquisa != null) {
            Rotina r = new Rotina().get();
            GenericaSessao.put("fisicaPesquisaGenerica", fisicaPesquisa);
            if (r.getId() == 469) {
                GenericaSessao.put("pesquisaFisicaTipo", "dependente");
                PF.update("form_campeonato_equipe");
            }
            fisicaPesquisa = new Fisica();
            fisica = new Fisica();
            closeModal();
        }
    }

    public Fisica getFisicaPesquisa() {
        return fisicaPesquisa;
    }

    public void setFisicaPesquisa(Fisica fisicaPesquisa) {
        this.fisicaPesquisa = fisicaPesquisa;
    }

    public String newRegister() {
        File f = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + getCliente() + "/temp/" + "foto/" + getUsuario().getId() + "/perfil.png"));
        if (f.exists()) {
            f.delete();
        }
        fisica = new Fisica();
        fisicaPesquisa = new Fisica();
        fisica.setEstadoCivil("Solteiro(a)");
        fisica.setNacionalidade("Brasileiro(a)");
        String nat;
        Cidade c = Registro.get().getFilial().getPessoa().getPessoaEndereco().getEndereco().getCidade();
        nat = c.getCidade();
        nat = nat + " - " + c.getUf();
        fisica.setNaturalidade(nat);
        return null;
    }

    public boolean existePessoaOposicaoPorDocumento(String documento) {
        if (!documento.isEmpty()) {
            OposicaoDao odbt = new OposicaoDao();
            return odbt.existPessoaDocumentoPeriodo(documento);
        }
        return false;
    }

    public List<Socios> getListSubSocios() {
        return listSubSocios;
    }

    public void setListSubSocios(List<Socios> listSubSocios) {
        this.listSubSocios = listSubSocios;
    }

    public String edit(Fisica f) {
        GenericaSessao.put("fisicaPesquisaEditar", f);
        GenericaSessao.put("editar", true);
        return ((ChamadaPaginaBean) GenericaSessao.getObject("chamadaPaginaBean")).pessoaFisica();
    }

}
