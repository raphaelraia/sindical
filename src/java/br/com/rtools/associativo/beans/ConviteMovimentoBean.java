package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.ConviteAutorizaCortesia;
import br.com.rtools.associativo.ConviteMovimento;
import br.com.rtools.associativo.ConviteServico;
import br.com.rtools.associativo.ConviteSuspencao;
import br.com.rtools.associativo.MatriculaSocios;
import br.com.rtools.associativo.Socios;
import br.com.rtools.associativo.db.ConviteDB;
import br.com.rtools.associativo.db.ConviteDBToplink;
import br.com.rtools.associativo.db.SociosDB;
import br.com.rtools.associativo.db.SociosDBToplink;
import br.com.rtools.endereco.Endereco;
import br.com.rtools.financeiro.CondicaoPagamento;
import br.com.rtools.financeiro.Evt;
import br.com.rtools.financeiro.FStatus;
import br.com.rtools.financeiro.FTipoDocumento;
import br.com.rtools.financeiro.Lote;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.Plano5;
import br.com.rtools.financeiro.ServicoValor;
import br.com.rtools.financeiro.TipoServico;
import br.com.rtools.financeiro.db.LoteDB;
import br.com.rtools.financeiro.db.LoteDBToplink;
import br.com.rtools.financeiro.db.MovimentoDB;
import br.com.rtools.financeiro.db.MovimentoDBToplink;
import br.com.rtools.financeiro.db.ServicoValorDB;
import br.com.rtools.financeiro.db.ServicoValorDBToplink;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.PessoaEndereco;
import br.com.rtools.pessoa.TipoDocumento;
import br.com.rtools.pessoa.dao.PessoaEnderecoDao;
import br.com.rtools.pessoa.db.SpcDB;
import br.com.rtools.pessoa.db.SpcDBToplink;
import br.com.rtools.seguranca.MacFilial;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ChamadaPaginaBean;
import br.com.rtools.sistema.SisPessoa;
import br.com.rtools.sistema.dao.SisPessoaDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Mask;
import br.com.rtools.utilitarios.Moeda;
import br.com.rtools.utilitarios.PF;
import br.com.rtools.utilitarios.PhotoCapture;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import org.apache.commons.io.FileUtils;

@ManagedBean
@SessionScoped
public class ConviteMovimentoBean implements Serializable {

    private ConviteMovimento conviteMovimento = new ConviteMovimento();
    private Movimento movimento = new Movimento();
    private Socios socios = new Socios();
    private Usuario usuario = new Usuario();
    private PessoaEndereco pessoaEndereco = new PessoaEndereco();
    private List<ConviteMovimento> conviteMovimentos = new ArrayList();
    private List<SelectItem> listPessoaAutoriza = new ArrayList();
    private List<SelectItem> conviteServicos = new ArrayList();

//    private StreamedContent fotoPerfilStreamed; //CARREGAR IMAGEM UPLOAD FOTO PHOTOCAM 
//    private StreamedContent fotoArquivoStreamed;
    private String message = "";
    private String tipoCaptura = "";
    private String descricaoPesquisa = "";
    private String comoPesquisa = "";
    private String porPesquisa = "todos";
    private String valorString = "";
    private String cliente = "";
    private int idServico = 0;
    private int idPessoaAutoriza = 0;
    private int idadeConvidado = 0;
    private boolean visibility = false;
    private boolean disabledConviteVencido = false;
    
    private boolean disabledValor = true;

    public ConviteMovimentoBean() {
        loadUsuario();
        //PhotoCapture.load("temp/convite/" + usuario.getId(), "form_convite:panel_foto");
    }

    public void novo() {
        valorString = "";
        idServico = 0;
        conviteMovimento = new ConviteMovimento();
        conviteMovimento.getSisPessoa().setDtNascimento(null);
        pessoaEndereco = new PessoaEndereco();
        socios = new Socios();
        message = "";
        tipoCaptura = "";
        descricaoPesquisa = "";
        comoPesquisa = "";
        porPesquisa = "todos";
        conviteMovimentos.clear();

        visibility = true;
        idadeConvidado = 0;
        movimento = new Movimento();
        listPessoaAutoriza.clear();
        
        loadValor();
    }

    public final void loadUsuario() {
        if (GenericaSessao.exists("sessaoUsuario")) {
            usuario = (Usuario) GenericaSessao.getObject("sessaoUsuario");
        }
    }

    public void atualizarCortesia() {
        idServico = 0;
        conviteServicos.clear();
        getConviteServicos();
        loadValor();
    }

    public void loadValor() {
        if (conviteMovimento.isCortesia() || conviteServicos.isEmpty() || conviteMovimento.getSisPessoa().getNascimento().isEmpty()) {
            disabledValor = true;
            valorString = "0,00";
            return;
        }

        Dao dao = new Dao();
        ServicoValorDB svdb = new ServicoValorDBToplink();
        try {
            DataHoje dh = new DataHoje();
            ServicoValor sv = (ServicoValor) svdb.pesquisaServicoValorPorIdade(((ConviteServico) dao.find(new ConviteServico(), Integer.parseInt(conviteServicos.get(idServico).getDescription()))).getServicos().getId(), dh.calcularIdade(conviteMovimento.getSisPessoa().getNascimento()));
            valorString = Moeda.converteR$(Float.toString((sv.getValor())));

            if (sv.getServicos().isAlterarValor()) {
                disabledValor = false;
            } else {
                disabledValor = true;
            }

        } catch (NumberFormatException e) {
            disabledValor = true;
            valorString = "0,00";
        }
    }

    public void openDialog() {
        visibility = true;
        PF.update("form_convite");
    }

    public void close() {
        novo();
        visibility = false;
        PF.update("form_convite");
    }

    public boolean validaSave() {
        if (MacFilial.getAcessoFilial().getId() == -1) {
            if (conviteMovimento.getId() == -1) {
                if (!conviteMovimento.isCortesia()) {
                    message = "Para salvar convites não cortesia configurar Filial em sua estação trabalho!";
                    return false;
                }
            }
        }

        if (conviteServicos.isEmpty()) {
            message = "Cadastrar serviços!";
            return false;
        }

        if (conviteMovimento.getPessoa().getId() == -1) {
            message = "Pesquisar sócio!";
            return false;
        }
        if (conviteMovimento.getSisPessoa().getNome().isEmpty()) {
            message = "Informar nome do convidado!";
            return false;
        }

        if (conviteMovimento.getSisPessoa().getNascimento().isEmpty()) {
            message = "Informar data de nascimento do convidado!";
            return false;
        }
        return true;
    }

    public boolean validaSaveConvite() {
        SpcDB spcDB = new SpcDBToplink();
        if (spcDB.existeRegistroPessoaSPC(conviteMovimento.getPessoa())) {
            message = "Existem débitos com o síndicato!";
            return false;
        }

        Dao dao = new Dao();
        Registro r = (Registro) dao.find(new Registro(), 1);
        ConviteDB cdb = new ConviteDBToplink();
        if (!conviteMovimento.isCortesia()) {
            if (cdb.limiteConvitePorSocio(r.getConviteQuantidadeSocio(), r.getConviteDiasSocio(), conviteMovimento.getPessoa().getId())) {
                message = "Limite de convites excedido para este sócio! Este sócio tem direito a disponibilizar " + r.getConviteQuantidadeSocio() + " convite(s) a cada " + r.getConviteDiasSocio() + "dia(s)";
                return false;
            }

            if (cdb.limiteConviteConvidado(r.getConviteQuantidadeConvidado(), r.getConviteDiasConvidado(), conviteMovimento.getSisPessoa().getId())) {
                message = "Limite de convites excedido para convidado! Este convidado tem direito a " + r.getConviteQuantidadeConvidado() + " a cada " + r.getConviteDiasConvidado() + "dia(s)";
                return false;
            }
            
            if (valorString.equals("")) {
                message = "Informar o valor do serviço, faixa etária não possuí valor do serviço!";
                return false;
            }
        }

        if (cdb.socio(conviteMovimento.getSisPessoa())) {
            message = "Convidado não pode ser sócio ativo!";
            return false;
        }

        ConviteSuspencao cs = new ConviteSuspencao();
        cs.setSisPessoa(conviteMovimento.getSisPessoa());
        if (cdb.existeSisPessoaSuspensa(cs)) {
            message = "Convidado possui cadastro suspenso!";
            return false;
        }

        SociosDB sdb = new SociosDBToplink();
        if (sdb.socioDebito(conviteMovimento.getPessoa().getId())) {
            message = "Sócio possui débitos!";
            return false;
        }
        return true;
    }

    public String save() {
        if (!validaSave()) {
            return null;
        }

        conviteMovimento.getSisPessoa().setNome(conviteMovimento.getSisPessoa().getNome().toUpperCase());

        Dao dao = new Dao();
        if (conviteMovimento.isCortesia()) {
            conviteMovimento.setAutorizaCortesia((ConviteAutorizaCortesia) dao.find(new ConviteAutorizaCortesia(), Integer.parseInt(listPessoaAutoriza.get(idPessoaAutoriza).getDescription())));
        } else {
            conviteMovimento.setAutorizaCortesia(null);
        }

        conviteMovimento.setConviteServico((ConviteServico) dao.find(new ConviteServico(), Integer.parseInt(conviteServicos.get(idServico).getDescription())));
        
        if (conviteMovimento.getSisPessoa().getEndereco() == null || conviteMovimento.getSisPessoa().getEndereco().getId() == -1) {
            conviteMovimento.getSisPessoa().setEndereco(null);
        }

        NovoLog novoLog = new NovoLog();

        dao.openTransaction();
        // SALVAR sis_pessoa ------------------------
        if (conviteMovimento.getSisPessoa().getId() == -1) {
            conviteMovimento.getSisPessoa().setTipoDocumento((TipoDocumento) dao.find(new TipoDocumento(), 1));
            if (!dao.save(conviteMovimento.getSisPessoa())) {
                dao.rollback();
                message = "Erro ao inserir sis pessoa!";
                return null;
            }

            dao.commit();
        } else {
            if (!dao.update(conviteMovimento.getSisPessoa())) {
                dao.rollback();
                message = "Erro ao atualizar sis pessoa!";
                return null;
            }

            dao.commit();
        }
        // FIM SALVAR sis_pessoa ------------------------

        DataHoje dh = new DataHoje();
        conviteMovimento.setValidade(dh.incrementarMeses(1, DataHoje.data()));


        // SALVAR CONVITE -----------------------------
        dao.openTransaction();
        if (conviteMovimento.getId() == -1) {
            
            if (!validaSaveConvite()) {
                return null;
            }
            
            conviteMovimento.setUsuario(usuario);
            conviteMovimento.setEvt(null);
            conviteMovimento.setDepartamento(null);
            conviteMovimento.setUsuarioInativacao(null);

            if (!dao.save(conviteMovimento)) {
                dao.rollback();
                message = "Erro ao inserir registro!";
                return null;
            }

            if (conviteMovimento.isCortesia()) {
                novoLog.save(""
                        + "ID: " + conviteMovimento.getId()
                        + " - Emissão: " + conviteMovimento.getEmissao()
                        + " - SisPessoa: (" + conviteMovimento.getSisPessoa().getId() + ") " + conviteMovimento.getSisPessoa().getNome()
                        + " - Responsável (Pessoa): (" + conviteMovimento.getPessoa().getId() + ") " + conviteMovimento.getPessoa().getNome()
                        + " - Validade: " + conviteMovimento.getValidade()
                        + (conviteMovimento.getAutorizaCortesia() != null ? " - Autorizado por (Pessoa): (" + conviteMovimento.getAutorizaCortesia().getId() + ") " + conviteMovimento.getAutorizaCortesia().getPessoa().getNome() : "")
                        + (conviteMovimento.getConviteServico() != null ? " - Convite Serviço: (" + conviteMovimento.getConviteServico().getId() + ") " + conviteMovimento.getConviteServico().getServicos().getDescricao() : " - Convite Serviço: (null)")
                );
            } else {
                float valor = Moeda.substituiVirgulaFloat(valorString);
                if (valor > 0) {
                    try {
                        if (!gerarMovimento(dao)) {
                            dao.rollback();
                            message = "Erro ao inserir registro!";
                            return null;
                        }
                    } catch (Exception e) {
                        dao.rollback();
                        message = "Erro ao inserir registro!";
                        return null;
                    }
                }
                novoLog.save(""
                        + "ID: " + conviteMovimento.getId()
                        + " - Emissão: " + conviteMovimento.getEmissao()
                        + " - SisPessoa: (" + conviteMovimento.getSisPessoa().getId() + ") " + conviteMovimento.getSisPessoa().getNome()
                        + " - Responsável (Pessoa): (" + conviteMovimento.getPessoa().getId() + ") " + conviteMovimento.getPessoa().getNome()
                        + " - Validade: " + conviteMovimento.getValidade()
                        + (conviteMovimento.getConviteServico() != null ? " - Convite Serviço: (" + conviteMovimento.getConviteServico().getId() + ") " + conviteMovimento.getConviteServico().getServicos().getDescricao() : " - Convite Serviço: (null)")
                );
            }

            message = "Registro inserido com sucesso";
        } else {
            ConviteMovimento cm = (ConviteMovimento) dao.find(new ConviteMovimento(), conviteMovimento.getId());
            String beforeUpdate = ""
                    + "ID: " + cm.getId()
                    + " - Emissão: " + cm.getEmissao()
                    + " - SisPessoa: (" + cm.getSisPessoa().getId() + ") " + cm.getSisPessoa().getNome()
                    + " - Responsável (Pessoa): (" + cm.getPessoa().getId() + ") " + cm.getPessoa().getNome()
                    + " - Validade: " + cm.getValidade()
                    + (cm.getAutorizaCortesia() != null ? " - Autorizado por (Pessoa): (" + cm.getAutorizaCortesia().getId() + ") " + cm.getAutorizaCortesia().getPessoa().getNome() : "")
                    + (cm.getConviteServico() != null ? " - Convite Serviço: (" + cm.getConviteServico().getId() + ") " + cm.getConviteServico().getServicos().getDescricao() : " - Convite Serviço: (null)");

            if (!dao.update(conviteMovimento)) {
                dao.rollback();
                message = "Erro ao atualizar registro!";
                return null;
            }

            novoLog.update(beforeUpdate, ""
                    + "ID: " + conviteMovimento.getId()
                    + " - Emissão: " + conviteMovimento.getEmissao()
                    + " - SisPessoa: (" + conviteMovimento.getSisPessoa().getId() + ") " + conviteMovimento.getSisPessoa().getNome()
                    + " - Responsável (Pessoa): (" + conviteMovimento.getPessoa().getId() + ") " + conviteMovimento.getPessoa().getNome()
                    + " - Validade: " + conviteMovimento.getValidade()
                    + (conviteMovimento.getAutorizaCortesia() != null ? " - Autorizado por (Pessoa): (" + conviteMovimento.getAutorizaCortesia().getId() + ") " + conviteMovimento.getAutorizaCortesia().getPessoa().getNome() : "")
                    + (conviteMovimento.getConviteServico() != null ? " - Convite Serviço: (" + conviteMovimento.getConviteServico().getId() + ") " + conviteMovimento.getConviteServico().getServicos().getDescricao() : " - Convite Serviço: (null)")
            );
            message = "Registro atualizado com sucesso";
        }
        dao.commit();
        // FIM SALVAR CONVITE -----------------------------

        NovoLog log = new NovoLog();
        log.save(conviteMovimento.toString());

//        if (getMovimento().getId() != -1) {
//            if (getMovimento().getBaixa() == null) {
//                List listMovimento = new ArrayList();
//                getMovimento();
//                movimento.setValorBaixa(movimento.getValor());
//                listMovimento.add(movimento);
//                GenericaSessao.put("listaMovimento", listMovimento);
//                GenericaSessao.put("caixa_banco", "caixa");
//                return ((ChamadaPaginaBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("chamadaPaginaBean")).baixaGeral();
//            }
//        }
        return null;
    }

    public boolean copiarArquivos(String path_arquivo, String path_destino) {
        try {
            FileInputStream origem;
            FileOutputStream destino;

            FileChannel fcOrigem;
            FileChannel fcDestino;

            origem = new FileInputStream(path_arquivo); // ARQUIVO QUE VOCÊ QUER COPIAR
            destino = new FileOutputStream(path_destino); // ONDE A COPIA SERÁ SALVA

            fcOrigem = origem.getChannel();
            fcDestino = destino.getChannel();

            fcOrigem.transferTo(0, fcOrigem.size(), fcDestino);

            origem.close();
            destino.close();
            return true;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ConviteMovimentoBean.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (IOException ex) {
            Logger.getLogger(ConviteMovimentoBean.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public void pesquisaSisPessoaDocumento() {
        //if (conviteMovimento.getSisPessoa().getId() == -1) {
        // APENAS COM CPF
        SisPessoaDao sisPessoaDB = new SisPessoaDao();
        if (!conviteMovimento.getSisPessoa().getDocumento().isEmpty()) {
            SisPessoa sp = sisPessoaDB.sisPessoaExiste(conviteMovimento.getSisPessoa(), true);
            if (sp != null) {
                conviteMovimento.setSisPessoa(sp);
            } else {
                String d = conviteMovimento.getSisPessoa().getDocumento();
                conviteMovimento.setSisPessoa(new SisPessoa());
                conviteMovimento.getSisPessoa().setDocumento(d);
            }
        }
        conviteMovimento.getSisPessoa().setNome(conviteMovimento.getSisPessoa().getNome().toUpperCase());
        //}
        loadValor();
    }

    public void pesquisaSisPessoaNomeNascimento() {
        if (conviteMovimento.getSisPessoa().getId() == -1) {
            // NOME / DATA DE NASCIMENTO    
            SisPessoaDao sisPessoaDB = new SisPessoaDao();
            if (!conviteMovimento.getSisPessoa().getNome().isEmpty() && !conviteMovimento.getSisPessoa().getNascimento().isEmpty()) {
                SisPessoa sp = sisPessoaDB.sisPessoaExiste(conviteMovimento.getSisPessoa());
                if (sp != null) {
                    conviteMovimento.setSisPessoa(sp);
                }
            }
            conviteMovimento.getSisPessoa().setNome(conviteMovimento.getSisPessoa().getNome().toUpperCase());
        }
        loadValor();
    }

    public void delete() {
        String msg = "";
        if (conviteMovimento.getId() != -1) {
            Dao dao = new Dao();
            conviteMovimento.setUsuarioInativacao((Usuario) dao.find(new Usuario(), getUsuario().getId()));
            conviteMovimento.setAtivo(false);
            dao.openTransaction();
            if (!dao.update(conviteMovimento)) {
                dao.rollback();
                message = "Erro ao inativar registro!";
                return;
            }

            if (getMovimento().getId() != -1) {
                getMovimento().setAtivo(false);
                if (!dao.update(getMovimento())) {
                    dao.rollback();
                    return;
                }
            }

            NovoLog novoLog = new NovoLog();
            novoLog.delete(""
                    + "ID: " + conviteMovimento.getId()
                    + " - Emissão: " + conviteMovimento.getEmissao()
                    + " - SisPessoa: (" + conviteMovimento.getSisPessoa().getId() + ") " + conviteMovimento.getSisPessoa().getNome()
                    + " - Responsável (Pessoa): (" + conviteMovimento.getPessoa().getId() + ") " + conviteMovimento.getPessoa().getNome()
                    + " - Validade: " + conviteMovimento.getValidade()
                    + (conviteMovimento.getAutorizaCortesia() != null ? " - Autorizado por (Pessoa): (" + conviteMovimento.getAutorizaCortesia().getId() + ") " + conviteMovimento.getAutorizaCortesia().getPessoa().getNome() : "")
                    + (conviteMovimento.getConviteServico() != null ? " - Convite Serviço: (" + conviteMovimento.getConviteServico().getId() + ") " + conviteMovimento.getConviteServico().getServicos().getDescricao() : " - Convite Serviço: (null)")
            );

            apagarImagem("perfil", dao);
            apagarImagem("documento", dao);

            dao.commit();
            msg = "Registro inativado com sucesso";
        }
        novo();
        message = msg;
    }

    public void edit(ConviteMovimento cm) {
        conviteMovimento = (ConviteMovimento) new Dao().find(cm);
        getConviteMovimento();
        carregaSocio(conviteMovimento.getPessoa());
        carregaEndereco(conviteMovimento.getPessoa());

        if (conviteMovimento.getSisPessoa().getEndereco() == null) {
            conviteMovimento.getSisPessoa().setEndereco(new Endereco());
        }

        if (conviteMovimento.getAutorizaCortesia() != null) {
            listPessoaAutoriza.clear();
            for (int i = 0; i < getListPessoaAutoriza().size(); i++) {
                if (Integer.parseInt(getListPessoaAutoriza().get(i).getDescription()) == conviteMovimento.getAutorizaCortesia().getId()) {
                    idPessoaAutoriza = i;
                    break;
                }
            }
        }

        getConviteServicos();
        if (conviteMovimento.getConviteServico() != null) {
            for (int i = 0; i < conviteServicos.size(); i++) {
                if (Integer.parseInt(conviteServicos.get(i).getDescription()) == conviteMovimento.getConviteServico().getId()) {
                    idServico = i;
                    break;
                }
            }
        }

        loadValor();

        visibility = true;
    }

    public List<ConviteMovimento> getConviteMovimentos() {
        if (conviteMovimentos.isEmpty()) {
            if (porPesquisa.equals("todos")) {
                descricaoPesquisa = "";
            }
            ConviteDB conviteDB = new ConviteDBToplink();
            conviteMovimentos = (List<ConviteMovimento>) conviteDB.pesquisaConviteMovimento(descricaoPesquisa, porPesquisa, comoPesquisa);
        }
        return conviteMovimentos;
    }

    public void setConviteMovimentos(List<ConviteMovimento> conviteMovimentos) {
        this.conviteMovimentos = conviteMovimentos;
    }

    public ConviteMovimento getConviteMovimento() {
        if (conviteMovimento.getId() != -1) {
            visibility = true;
        }

        if (GenericaSessao.exists("fisicaPesquisa")) {
            Pessoa p = (Pessoa) ((Fisica) GenericaSessao.getObject("fisicaPesquisa", true)).getPessoa();
            conviteMovimento.setPessoa(p);
            carregaSocio(p);
            visibility = true;
            carregaEndereco(p);
        }

        if (GenericaSessao.exists("sisPessoaPesquisa")) {
            conviteMovimento.setSisPessoa(((SisPessoa) GenericaSessao.getObject("sisPessoaPesquisa", true)));
            visibility = true;
        }

        if (GenericaSessao.exists("enderecoPesquisa")) {
            conviteMovimento.getSisPessoa().setEndereco((Endereco) GenericaSessao.getObject("enderecoPesquisa", true));
            visibility = true;
        }
        return conviteMovimento;
    }

    public void setConviteMovimento(ConviteMovimento conviteMovimento) {
        this.conviteMovimento = conviteMovimento;
    }

    public Socios getSocios() {
        return socios;
    }

    public void setSocios(Socios socios) {
        this.socios = socios;
    }

    public void carregaSocio(Pessoa p) {
        SociosDB dB = new SociosDBToplink();
        socios = dB.pesquisaSocioPorPessoa(p.getId());
    }

    public void carregaEndereco(Pessoa p) {
        PessoaEnderecoDao dao = new PessoaEnderecoDao();
        int idEndereco[] = new int[]{1, 2, 3, 4};
        for (int i = 0; i < idEndereco.length; i++) {
            pessoaEndereco = (PessoaEndereco) dao.pesquisaEndPorPessoaTipo(p.getId(), idEndereco[i]);
            if (pessoaEndereco == null) {
                pessoaEndereco = new PessoaEndereco();
            } else {
                break;
            }
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<SelectItem> getConviteServicos() {
        if (conviteServicos.isEmpty()) {
            Dao dao = new Dao();
            List<ConviteServico> list = new ConviteDBToplink().listaConviteServicoCortesia(conviteMovimento.isCortesia());
            int i = 0;
            for (ConviteServico cs : list) {
                List listSemana = new ArrayList();
                if (cs.isDomingo()) {
                    listSemana.add("Dom");
                }
                if (cs.isSegunda()) {
                    listSemana.add("Seg");
                }
                if (cs.isTerca()) {
                    listSemana.add("Ter");
                }
                if (cs.isQuarta()) {
                    listSemana.add("Qua");
                }
                if (cs.isQuinta()) {
                    listSemana.add("Qui");
                }
                if (cs.isSexta()) {
                    listSemana.add("Sex");
                }
                if (cs.isSabado()) {
                    listSemana.add("Sáb");
                }
                if (cs.isFeriado()) {
                    listSemana.add("Feriado");
                }
                conviteServicos.add(new SelectItem(i, cs.getServicos().getDescricao() + " " + listSemana, "" + cs.getId()));
                i++;
            }
        }
        return conviteServicos;
    }

    public void setConviteServicos(List<SelectItem> conviteServicos) {
        this.conviteServicos = conviteServicos;
    }

    public PessoaEndereco getPessoaEndereco() {
        return pessoaEndereco;
    }

    public void setPessoaEndereco(PessoaEndereco pessoaEndereco) {
        this.pessoaEndereco = pessoaEndereco;
    }

    public String getTipoCaptura() {
        return tipoCaptura;
    }

    public void setTipoCaptura(String tipoCaptura) {
        this.tipoCaptura = tipoCaptura;
    }

    public void capturarTipo(String tipoCaptura) {
        this.tipoCaptura = tipoCaptura;
        if (tipoCaptura.equals("perfil")) {
            new PhotoCapture().openAndSave(conviteMovimento.getSisPessoa(), "perfil", "form_convite:panel_foto");
        } else {
            new PhotoCapture().openAndSave(conviteMovimento.getSisPessoa(), "documento", "form_convite:panel_foto");
        }
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public boolean isVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    public int getIdServico() {
        return idServico;
    }

    public void setIdServico(int idServico) {
        this.idServico = idServico;
    }

    public void apagarImagem(String tipoCaptura, Dao dao) {
        if (tipoCaptura.equals("perfil")) {
            File fsave = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/resources/cliente/" + getCliente().toLowerCase() + "/imagens/sispessoa/" + conviteMovimento.getSisPessoa().getId() + "/perfil/" + conviteMovimento.getSisPessoa().getFotoPerfil() + ".png"));
            if (fsave.exists()) {
                FileUtils.deleteQuietly(fsave);

                conviteMovimento.getSisPessoa().setFotoPerfil("");
                if (dao == null) {
                    Dao daox = new Dao();
                    daox.openTransaction();
                    daox.update(conviteMovimento.getSisPessoa());
                    daox.commit();
                } else {
                    dao.update(conviteMovimento.getSisPessoa());
                }
            }
        } else {
            File fsave = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/resources/cliente/" + getCliente().toLowerCase() + "/imagens/sispessoa/" + conviteMovimento.getSisPessoa().getId() + "/documento/" + conviteMovimento.getSisPessoa().getFotoArquivo() + ".png"));
            if (fsave.exists()) {
                FileUtils.deleteQuietly(fsave);

                conviteMovimento.getSisPessoa().setFotoArquivo("");
                if (dao == null) {
                    Dao daox = new Dao();
                    daox.openTransaction();
                    daox.update(conviteMovimento.getSisPessoa());
                    daox.commit();
                } else {
                    dao.update(conviteMovimento.getSisPessoa());
                }
            }
        }
    }

    public int getIdPessoaAutoriza() {
        return idPessoaAutoriza;
    }

    public void setIdPessoaAutoriza(int idPessoaAutoriza) {
        this.idPessoaAutoriza = idPessoaAutoriza;
    }

    public List<SelectItem> getListPessoaAutoriza() {
        if (listPessoaAutoriza.isEmpty()) {
            ConviteDB db = new ConviteDBToplink();
            List<ConviteAutorizaCortesia> list = db.listaConviteAutorizaCortesia(conviteMovimento.getId() == -1);

            int i = 0;
            for (ConviteAutorizaCortesia cac : list) {
                listPessoaAutoriza.add(new SelectItem(i, cac.getPessoa().getNome(), "" + cac.getId()));
                i++;
            }
        }
        return listPessoaAutoriza;
    }

    public void setListPessoaAutoriza(List<SelectItem> listPessoaAutoriza) {
        this.listPessoaAutoriza = listPessoaAutoriza;
    }
//
//    public String validadeConvite(String dataEmissao) {
//        DataHoje dh = new DataHoje();
//        dataEmissao = (String) dh.incrementarMeses(1, dataEmissao);
//        return dataEmissao;
//    }

    public String getMascara() {
        String mask = porPesquisa;
        if (porPesquisa.equals("socioCPF")) {
            mask = "cpf";
        }
        return Mask.getMascaraPesquisa(mask, true);
    }

    public void acaoPesquisaInicial() {
        setComoPesquisa("Inicial");
    }

    public void acaoPesquisaParcial() {
        setComoPesquisa("Parcial");
    }

    public String getDescricaoPesquisa() {
        return descricaoPesquisa;
    }

    public void setDescricaoPesquisa(String descricaoPesquisa) {
        this.descricaoPesquisa = descricaoPesquisa;
    }

    public String getComoPesquisa() {
        return comoPesquisa;
    }

    public void setComoPesquisa(String comoPesquisa) {
        this.comoPesquisa = comoPesquisa;
    }

    public String getPorPesquisa() {
        return porPesquisa;
    }

    public void setPorPesquisa(String porPesquisa) {
        this.porPesquisa = porPesquisa;
    }

    public int getIdadeConvidado() {
        if (!conviteMovimento.getSisPessoa().getNascimento().equals("")) {
            //if (idadeConvidado == 0) {
            DataHoje dh = new DataHoje();
            idadeConvidado = (int) dh.calcularIdade(conviteMovimento.getSisPessoa().getNascimento());
            //}
        }
        return idadeConvidado;
    }

    public void setIdadeConvidado(int idadeConvidado) {
        this.idadeConvidado = idadeConvidado;
    }

    public String getValorString() {
        return valorString;
    }

    public void setValorString(String valorString) {
        this.valorString = valorString;
    }

    public void gerarMovimento() {
        Dao dao = new Dao();
        dao.openTransaction();
        gerarMovimento(dao);
    }

    public boolean gerarMovimento(Dao dao) {
        if (conviteMovimento.getEvt() == null) {
            String vencimento = conviteMovimento.getEmissao();
            String referencia;
            Plano5 plano5 = conviteMovimento.getConviteServico().getServicos().getPlano5();
            FTipoDocumento fTipoDocumento = (FTipoDocumento) dao.find(new FTipoDocumento(), 13);
            float valor = Moeda.substituiVirgulaFloat(valorString);
            Lote lote = new Lote(
                    -1,
                    (Rotina) dao.find(new Rotina(), 215),
                    "R",
                    DataHoje.data(),
                    conviteMovimento.getPessoa(),
                    plano5,
                    false,
                    "",
                    valor,
                    conviteMovimento.getConviteServico().getServicos().getFilial(),
                    null,
                    null,
                    "",
                    (FTipoDocumento) dao.find(new FTipoDocumento(), 13),
                    (CondicaoPagamento) dao.find(new CondicaoPagamento(), 1),
                    (FStatus) dao.find(new FStatus(), 1),
                    null,
                    false,
                    0
            );
            try {
                String nrCtrBoletoResp = "";
                for (int x = 0; x < (Integer.toString(conviteMovimento.getPessoa().getId())).length(); x++) {
                    nrCtrBoletoResp += 0;
                }
                nrCtrBoletoResp += conviteMovimento.getPessoa().getId();
                String mes = conviteMovimento.getEmissao().substring(3, 5);
                String ano = conviteMovimento.getEmissao().substring(6, 10);
                referencia = mes + "/" + ano;
                Evt evt = new Evt();
                evt.setDescricao("CONVITE MOVIMENTO");
                if (!dao.save(evt)) {
                    return false;
                }
                lote.setEvt(evt);
                if (!dao.save(lote)) {
                    return false;
                }
                String nrCtrBoleto = nrCtrBoletoResp + Long.toString(DataHoje.calculoDosDias(DataHoje.converte("07/10/1997"), DataHoje.converte(vencimento)));
                movimento = new Movimento(
                        -1,
                        lote,
                        plano5,
                        conviteMovimento.getPessoa(),
                        conviteMovimento.getConviteServico().getServicos(),
                        null,
                        (TipoServico) dao.find(new TipoServico(), 1),
                        null,
                        valor,
                        referencia,
                        conviteMovimento.getEmissao(),
                        1,
                        true,
                        "E",
                        false,
                        conviteMovimento.getPessoa(), // TITULAR / RESPONSÁVEL
                        conviteMovimento.getPessoa(), // BENEFICIÁRIO
                        "",
                        nrCtrBoleto,
                        vencimento,
                        0,
                        0,
                        0,
                        0,
                        0,
                        0,
                        0,
                        fTipoDocumento,
                        0, new MatriculaSocios()
                );
                if (dao.save(movimento)) {
                    conviteMovimento.setEvt(evt);
                    return dao.update(conviteMovimento);
                } else {
                    return false;
                }
            } catch (Exception e) {
            }
        }
        return false;
    }

    public String getCliente() {
        if (cliente.equals("")) {
            if (GenericaSessao.exists("sessaoCliente")) {
                return GenericaSessao.getString("sessaoCliente");
            }
        }
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String baixarMovimento() {
        if (getMovimento().getId() != -1) {
            List list = new ArrayList();
            movimento.setValorBaixa(movimento.getValor());
            list.add(movimento);
            GenericaSessao.put("listaMovimento", list);
            GenericaSessao.put("caixa_banco", "caixa");
            return ((ChamadaPaginaBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("chamadaPaginaBean")).baixaGeral();
        }
        return null;
    }

    public Movimento getMovimento() {
        if (conviteMovimento.getEvt() != null) {
            LoteDB loteDB = new LoteDBToplink();
            Lote lote = (Lote) loteDB.pesquisaLotePorEvt(conviteMovimento.getEvt());
            MovimentoDB mdb = new MovimentoDBToplink();
            List<Movimento> movimentos = (List<Movimento>) mdb.listaMovimentosDoLote(lote.getId());
            for (Movimento m : movimentos) {
                movimento = m;
                break;
            }
        }
        return movimento;
    }

    public void setMovimento(Movimento movimento) {
        this.movimento = movimento;
    }

    public boolean isDisabledValor() {
        return disabledValor;
    }

    public void setDisabledValor(boolean disabledValor) {
        this.disabledValor = disabledValor;
    }

    public boolean isDisabledConviteVencido() {
        if (DataHoje.menorData(conviteMovimento.getValidade(), DataHoje.data())) {
            disabledConviteVencido = true;
        } else {
            disabledConviteVencido = false;
        }
        return disabledConviteVencido;
    }

    public void setDisabledConviteVencido(boolean disabledConviteVencido) {
        this.disabledConviteVencido = disabledConviteVencido;
    }

    public boolean isRenderedImpressao() {
        if (conviteMovimento.getId() != -1) {
            if (conviteMovimento.isCortesia()) {
                return true;
            }

            if (Moeda.converteUS$(valorString) <= 0) {
                return true;
            }

            if (getMovimento().getBaixa() != null) {
                return true;
            }
        }
        return false;
    }
}
