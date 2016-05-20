package br.com.rtools.atendimento.beans;

import br.com.rtools.atendimento.AteMovimento;
import br.com.rtools.atendimento.AteOperacao;
import br.com.rtools.atendimento.AteStatus;
import br.com.rtools.atendimento.dao.AtendimentoDao;
import br.com.rtools.homologacao.OperacaoDepartamento;
import br.com.rtools.homologacao.Senha;
import br.com.rtools.homologacao.dao.OperacaoDao;
import br.com.rtools.homologacao.db.HomologacaoDB;
import br.com.rtools.homologacao.db.HomologacaoDBToplink;
import br.com.rtools.impressao.ParametroSenha;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.TipoDocumento;
import br.com.rtools.pessoa.db.FisicaDB;
import br.com.rtools.pessoa.db.FisicaDBToplink;
import br.com.rtools.seguranca.Departamento;
import br.com.rtools.seguranca.MacFilial;
import br.com.rtools.seguranca.PermissaoUsuario;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.seguranca.dao.PermissaoUsuarioDao;
import br.com.rtools.sistema.SisPessoa;
import br.com.rtools.utilitarios.AnaliseString;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.Diretorio;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.SalvaArquivos;
import br.com.rtools.utilitarios.ValidaDocumentos;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@ManagedBean
@SessionScoped
public class AtendimentoBean implements Serializable {
    
    private AteOperacao ateOperacao = new AteOperacao();
    private AteMovimento ateMovimento = new AteMovimento();
    private SisPessoa atePessoa = new SisPessoa();
    private String porPesquisa = "hoje";
    private MacFilial macFilial = new MacFilial();
    private Filial filial = new Filial();
    private int idIndexPessoa = -1;
    private int idIndexMovimento = -1;
    private List<AteMovimento> listaAteMovimento = new ArrayList();
    private List<SisPessoa> listaAtePessoas = new ArrayList();
    private int idFilial = 0;
    private int idOperacao = 0;
    private List<SelectItem> listaAtendimentoOperacoes = new ArrayList<SelectItem>();
    private List<SelectItem> listaFiliais = new ArrayList<SelectItem>();
    
    private String horaEmissaoString = "";
    private Juridica empresa = new Juridica();
    private Usuario usuario = new Usuario();
    
    private SisPessoa sisPessoa = new SisPessoa();
    private SisPessoa sisPessoaAtualiza = new SisPessoa();
    private StreamedContent fileDownload = null;
    private boolean visibleModal = false;
    private String tipoTelefone = "telefone";
    
    private List<SelectItem> listaUsuarios = new ArrayList<SelectItem>();
    private int index_usuario = 0;
    private boolean chkReserva = false;
    
    public AtendimentoBean() {
        usuario = (Usuario) GenericaSessao.getObject("sessaoUsuario");
    }
    
    public void alterarTipoMascara() {
        if (tipoTelefone.equals("telefone")) {
            tipoTelefone = "celular";
        } else {
            tipoTelefone = "telefone";
        }
    }
    
    public SisPessoa getSisPessoa() {
        return sisPessoa;
    }
    
    public void setSisPessoa(SisPessoa sisPessoa) {
        this.sisPessoa = sisPessoa;
    }
    
    public void pesquisaCPFeOPOSICAO() {
        if (sisPessoa.getFisica().getId() == -1) {
            if (sisPessoa.getId() == -1) {
                if (sisPessoa.getDocumento().isEmpty()) {
                    return;
                }
                
                if (!ValidaDocumentos.isValidoCPF(AnaliseString.extrairNumeros(sisPessoa.getDocumento()))) {
                    sisPessoa.setDocumento("");
                    GenericaMensagem.warn("Atenção", "Esse documento é Inválido!!");
                    return;
                }
                
                FisicaDB fisicaDB = new FisicaDBToplink();
                List<Fisica> listf = fisicaDB.pesquisaFisicaPorDoc(sisPessoa.getDocumento());

                // SE NÃO ACHAR PESSOA FÍSICA, PESQUISAR EM SIS_PESSOA
                if (listf.isEmpty()) {
                    AtendimentoDao atendimentoDB = new AtendimentoDao();
                    SisPessoa spes = (SisPessoa) atendimentoDB.pessoaDocumento(sisPessoa.getDocumento());
                    
                    if (spes == null) {
                        return;
                    }
                    sisPessoa = spes;
                    verificaPessoaOposicao();
                    return;
                }
                
                Fisica fi = (Fisica) listf.get(0);
                
                sisPessoa.setNome(fi.getPessoa().getNome());
                sisPessoa.setDocumento(fi.getPessoa().getDocumento());
                sisPessoa.setTelefone(fi.getPessoa().getTelefone1());
                sisPessoa.setRg(fi.getRg());
                if (fi.getDtNascimento() == null) {
                    if (sisPessoa.getDtNascimento() == null) {
                        sisPessoa.setNascimento("01/01/1900");
                    }
                } else if (sisPessoa.getDtNascimento() == null) {
                    sisPessoa.setDtNascimento(fi.getDtNascimento());
                }
                verificaPessoaOposicao();
                try {
                    sisPessoa.setFisica(fi);
                } catch (Exception e) {
                    
                }
            }
        }
    }
    
    public void pesquisaRG() {
        if (sisPessoa.getFisica().getId() == -1) {
            if (sisPessoa.getId() == -1) {
                if (sisPessoa.getRg().isEmpty()) {
                    return;
                }
                
                FisicaDB fisicaDB = new FisicaDBToplink();
                List<Fisica> listf = fisicaDB.pesquisaFisicaPorDocRG(sisPessoa.getRg());

                // SE NÃO ACHAR PESSOA FÍSICA, PESQUISAR EM SIS_PESSOA
                if (listf.isEmpty()) {
                    AtendimentoDao atendimentoDB = new AtendimentoDao();
                    SisPessoa spes = (SisPessoa) atendimentoDB.pessoaDocumento(sisPessoa.getRg());
                    
                    if (spes == null) {
                        return;
                    }
                    sisPessoa = spes;
                    return;
                }
                
                Fisica fi = (Fisica) listf.get(0);
                
                sisPessoa.setNome(fi.getPessoa().getNome());
                sisPessoa.setDocumento(fi.getPessoa().getDocumento());
                sisPessoa.setTelefone(fi.getPessoa().getTelefone1());
                sisPessoa.setRg(fi.getRg());
                if (fi.getDtNascimento() == null) {
                    if (sisPessoa.getDtNascimento() == null) {
                        sisPessoa.setNascimento("01/01/1900");
                    }
                } else if (sisPessoa.getDtNascimento() == null) {
                    sisPessoa.setDtNascimento(fi.getDtNascimento());
                }
                try {
                    sisPessoa.setFisica(fi);
                } catch (Exception e) {
                    
                }
            }
        }
    }
    
    public void editarSisPessoa() {
        sisPessoaAtualiza = sisPessoa;
    }
    
    public void atualizaSisPessoa() {
        if (!sisPessoaAtualiza.getDocumento().isEmpty()) {
            if (!ValidaDocumentos.isValidoCPF(AnaliseString.extrairNumeros(sisPessoaAtualiza.getDocumento()))) {
                GenericaMensagem.warn("Atenção", "Esse documento é Inválido!!");
                return;
            }

//            FisicaDB fisicaDB = new FisicaDBToplink();
//            List<Fisica> listf = fisicaDB.pesquisaFisicaPorDoc(sisPessoaAtualiza.getDocumento());
//            
//            // DOCUMENTO JÁ EXISTE PARA OUTRA PESSOA FISICA
//            if (!listf.isEmpty()){
//                if (!listf.get(0).getPessoa().getNome().equals(sisPessoaAtualiza.getNome()) ){
//                    GenericaMensagem.warn("Atenção", "Esse documento já existe para outra Pessoa!");
//                    return;
//                }
//            }
            AtendimentoDao atendimentoDB = new AtendimentoDao();
            SisPessoa spes = (SisPessoa) atendimentoDB.pessoaDocumento(sisPessoaAtualiza.getDocumento());
            
            if (spes != null && spes.getId() != sisPessoaAtualiza.getId()) {
                GenericaMensagem.warn("Atenção", "Esse documento já existe para outra Pessoa!");
                return;
            }
        }
        
        Dao dao = new Dao();
        
        dao.openTransaction();
        
        if (!dao.update(sisPessoaAtualiza)) {
            dao.rollback();
            GenericaMensagem.error("Erro", "Não foi possivel atualizar cadastro!");
            return;
        }
        
        sisPessoa = sisPessoaAtualiza;
        sisPessoaAtualiza = new SisPessoa();
        dao.commit();
        
    }
    
    public String verSenha(AteMovimento atendimento) {
        AtendimentoDao db = new AtendimentoDao();
        Senha senha = db.pesquisaSenha(atendimento.getId());
        if (senha != null) {
            if (senha.getSenha() < 10) {
                return "0" + String.valueOf(senha.getSenha());
            } else {
                return String.valueOf(senha.getSenha());
            }
        }
        
        return "Sem Senha";
    }
    
    public String imprimirSenha(AteMovimento atendimento) throws JRException {
        
        AtendimentoDao db = new AtendimentoDao();
        
        Senha senha = db.pesquisaSenha(atendimento.getId());
        
        Collection lista = new ArrayList();
        
        if (senha.getId() != -1) {
            String logo_cliente = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoCliente.png");
            if (senha.getAteMovimento().getJuridica() != null) {
                lista.add(new ParametroSenha(logo_cliente,
                        senha.getFilial().getFilial().getPessoa().getNome(),
                        senha.getFilial().getFilial().getPessoa().getDocumento(),
                        senha.getAteMovimento().getJuridica().getPessoa().getNome(),
                        senha.getAteMovimento().getJuridica().getPessoa().getDocumento(),
                        "", // PREPOSTO
                        senha.getAteMovimento().getPessoa().getNome(),
                        senha.getUsuario().getPessoa().getNome(),
                        senha.getData(),
                        senha.getHora(),
                        String.valueOf(senha.getSenha())));
            } else {
                lista.add(new ParametroSenha(logo_cliente,
                        senha.getFilial().getFilial().getPessoa().getNome(),
                        senha.getFilial().getFilial().getPessoa().getDocumento(),
                        "", // NOME EMPRESA
                        "", // DOCUMENTO EMPRESA
                        "", // PREPOSTO
                        senha.getAteMovimento().getPessoa().getNome(),
                        senha.getUsuario().getPessoa().getNome(),
                        senha.getData(),
                        senha.getHora(),
                        String.valueOf(senha.getSenha())));
                
            }
        }
        
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new File((((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Relatorios/HOM_SENHA.jasper"))));
        JRBeanCollectionDataSource dtSource = new JRBeanCollectionDataSource(lista);
        JasperPrint print = JasperFillManager.fillReport(jasperReport, null, dtSource);
        byte[] arquivo = JasperExportManager.exportReportToPdf(print);
        String nomeDownload = "senha_" + DataHoje.hora().replace(":", "") + ".pdf";
        String pathPasta = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/senhas");
        Diretorio.criar("Arquivos/senhas");
        if (!new File(pathPasta).exists()) {
            File file = new File(pathPasta);
            file.mkdir();
        }
        SalvaArquivos salvaArquivos = new SalvaArquivos(arquivo, nomeDownload, false);
        salvaArquivos.salvaNaPasta(pathPasta);
        
        InputStream stream = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/senhas" + "/" + nomeDownload);
        fileDownload = new DefaultStreamedContent(stream, "application/pdf", nomeDownload);

//        Download download = new Download(nomeDownload, pathPasta, "application/pdf", FacesContext.getCurrentInstance());
//        download.baixar();
//        download.remover();
        return "atendimento";
    }
    
    public String novo() {
        GenericaSessao.put("atendimentoBean", new AtendimentoBean());
        return "atendimento";
    }
    
    public void fecharModal() {
        GenericaSessao.put("atendimentoBean", new AtendimentoBean());
    }
    
    public String salvarImprimir() {
        salvar(true);
        visibleModal = true;
        return null;
    }
    
    public void salvar(boolean imprimir) {
        
        if (ateMovimento.getFilial().getId() == -1) {
            GenericaMensagem.error("Erro", "Informe qual a sua Filial!");
            return;
        }
        
        if (!sisPessoa.getDocumento().isEmpty()) {
            if (!ValidaDocumentos.isValidoCPF(AnaliseString.extrairNumeros(sisPessoa.getDocumento()))) {
                GenericaMensagem.warn("Validação", "Informe um CPF válido!");
                return;
            }
        }
        
        if (sisPessoa.getDtNascimento() == null) {
            GenericaMensagem.warn("Validação", "Informe data de nascimento!");
            return;
        }
        
        if (sisPessoa.getNome().isEmpty()) {
            GenericaMensagem.warn("Validação", "Digite o NOME da pessoa!");
            return;
        }

//        if (empresa.getId() == -1){
//            //msg = "Pesquise uma Empresa para Agendar.";
//            GenericaMensagem.warn("Atenção", "Pesquise uma Empresa para concluir o Atendimento!");
//            return;
//        }
//        SisPessoa ap = atendimentoDB.pessoaDocumento(ateMovimento.getPessoa().getDocumento());
//        if (ap == null) {
//            ap = atendimentoDB.pessoaDocumento(ateMovimento.getPessoa().getRg());
//        }
        Dao dao = new Dao();
        
        sisPessoa.setTipoDocumento((TipoDocumento) dao.find(new TipoDocumento(), 1));
        sisPessoa.setEndereco(null);
        
        dao.openTransaction();
        if (sisPessoa.getId() == -1) {
            if (!dao.save(sisPessoa)) {
                dao.rollback();
                return;
            }
        } else if (!dao.update(sisPessoa)) {
            dao.rollback();
            return;
        }
        dao.commit();
        
        ateMovimento.setHoraEmissao(getHoraEmissaoString());
        ateMovimento.setFilial(filial);
        ateMovimento.setOperacao((AteOperacao) dao.find(new AteOperacao(), Integer.parseInt(listaAtendimentoOperacoes.get(idOperacao).getDescription())));
        ateMovimento.setStatus((AteStatus) dao.find(new AteStatus(), 1));
        ateMovimento.setJuridica((empresa == null || empresa.getId() == -1) ? null : empresa);
        ateMovimento.setPessoa(sisPessoa);
        ateMovimento.setAtendente(null);
        
        if (chkReserva && !listaUsuarios.isEmpty()) {
            PermissaoUsuario pu = (PermissaoUsuario) dao.find(new PermissaoUsuario(), Integer.valueOf(listaUsuarios.get(index_usuario).getDescription()));
            ateMovimento.setReserva(pu.getUsuario());
        } else {
            ateMovimento.setReserva(null);
        }
        
        dao.openTransaction();
        if (ateMovimento.getId() == -1) {
            ateMovimento.setHoraEmissao(getHoraEmissaoString());

            // PERMITIR QUE CRIE UM ATENDIMENTO REPETIDO PARA MESMA PESSOA -- chamado 287 --
//            if (atendimentoDB.existeAtendimento(ateMovimento)) {
//                GenericaMensagem.error("Atenção", "Atendimento já cadastrado!");
//                sv.desfazerTransacao();
//                return;
//            }
            if (!dao.save(ateMovimento)) {
                GenericaMensagem.error("Erro", "Não foi possivel salvar Atendimento!");
                dao.rollback();
                return;
            }
            
            OperacaoDepartamento od = new OperacaoDao().pesquisaOperacaoDepartamento(ateMovimento.getFilial().getId(), ateMovimento.getOperacao().getId());
            Departamento dep;
            if (od != null){
                dep = od.getDepartamento();
            }else{
                // CHAMADO 1363
                // caso não tenha definido uma Operação Departamento então setar o Departamento padrão (8) HOMOLOGAÇÃO
                // que já estava funcionando antes das alterações
                dep = (Departamento) dao.find(new Departamento(), 8);
            }
            HomologacaoDB dbh = new HomologacaoDBToplink();
            int ultima_senha = dbh.pesquisaUltimaSenha(filial.getId()) + 1;
            Senha senha = new Senha(-1, null, DataHoje.horaMinuto(), "", 0, usuario, DataHoje.data(), ultima_senha, filial, ateMovimento, null, null, null, dep);
            
            if (!dao.save(senha)) {
                GenericaMensagem.error("Erro", "Erro ao Salvar Senha!");
                dao.rollback();
                return;
            }
            
            dao.commit();
            
            if (imprimir) {
                try {
                    imprimirSenha(ateMovimento);
                } catch (JRException ex) {
                }
            } else {
                GenericaSessao.put("atendimentoBean", new AtendimentoBean());
            }
            
            GenericaMensagem.info("Sucesso", "Atendimento Salvo!");
        } else {
            if (!dao.update(ateMovimento)) {
                dao.rollback();
                GenericaMensagem.error("Erro", "Não foi possivel atualizar o Atendimento");
                return;
            }
            
            dao.commit();
            
            GenericaSessao.put("atendimentoBean", new AtendimentoBean());
            GenericaMensagem.info("Sucesso", "Atendimento Atualizado!");
        }
        
    }
    
    public String editar(AteMovimento am) {
        ateMovimento = am;
        sisPessoa = ateMovimento.getPessoa();
        for (int i = 0; i < listaAtendimentoOperacoes.size(); i++) {
            if (Integer.parseInt(listaAtendimentoOperacoes.get(i).getDescription()) == ateMovimento.getOperacao().getId()) {
                idOperacao = i;
            }
        }
        for (int i = 0; i < getListaFiliais().size(); i++) {
            if (Integer.parseInt(getListaFiliais().get(i).getDescription()) == ateMovimento.getFilial().getId()) {
                idFilial = i;
            }
        }
        empresa = ateMovimento.getJuridica();
        
        setHoraEmissaoString(ateMovimento.getHoraEmissao());
        verificaPessoaOposicao();
        
        chkReserva = ateMovimento.getReserva() != null;
        if (chkReserva) {
            for (int i = 0; i < getListaUsuarios().size(); i++) {
                PermissaoUsuario pu = (PermissaoUsuario) new Dao().find(new PermissaoUsuario(), Integer.valueOf(listaUsuarios.get(i).getDescription()));
                if (pu.getUsuario().getId() == ateMovimento.getReserva().getId()) {
                    index_usuario = i;
                }
            }
        }
        
        return null;
    }
    
    public void excluir() {
        Dao dao = new Dao();
        if (ateMovimento.getId() > 0) {
            AteMovimento ateMov = (AteMovimento) dao.find(new AteMovimento(), ateMovimento.getId());
            AtendimentoDao db = new AtendimentoDao();
            
            Senha senha = db.pesquisaSenha(ateMovimento.getId());
            
            dao.openTransaction();
            
            if (senha != null) {
                if (!dao.delete(senha)) {
                    dao.rollback();
                    GenericaMensagem.error("Erro", "Falha excluir Senha!");
                    return;
                }
            }
            
            if (dao.delete(ateMov)) {
                dao.commit();
                GenericaMensagem.info("Sucesso", "Atendimento Excluido!");
                novo();
                return;
            } else {
                dao.rollback();
                GenericaMensagem.error("Erro", "Falha ao excluir Atendimento!");
            }
            
        }
        getListaAteMovimento().clear();
    }
    
    public void cancelar() {
        Dao dao = new Dao();
        dao.openTransaction();

        //AteMovimento ateMov = (AteMovimento) sv.pesquisaObjeto(ateMovimento.getId(), "AteMovimento");
        // status 3 Atendimento Cancelado
        ateMovimento.setStatus((AteStatus) dao.find(new AteStatus(), 3));
        
        if (!dao.update(ateMovimento)) {
            dao.rollback();
        } else {
            dao.commit();
        }
    }
    
    public String retornaOposicaoPessoa(String documento) {
        AtendimentoDao atendimentoDB = new AtendimentoDao();
        if (atendimentoDB.pessoaOposicao(documento)) {
            return "tblOposicaox";
        } else {
            return "";
        }
    }
    
    public void verificaPessoaOposicao() {
        AtendimentoDao atendimentoDB = new AtendimentoDao();
        if (atendimentoDB.pessoaOposicao(sisPessoa.getDocumento())) {
            RequestContext.getCurrentInstance().execute("PF('dlg_mensagem_oposicao').show();");
        }
    }
//
//    public void verificaCPF(String tipoVerificacao) {
//        if (ateMovimento.getId() != -1 || ateMovimento.getPessoa().getId() != -1) {
//            return;
//        }
//        String valorPesquisa = "";
//        
//        if (tipoVerificacao.equals("cpf")) {
//            if (!ateMovimento.getPessoa().getDocumento().isEmpty()) {
//                if (!ValidaDocumentos.isValidoCPF(AnaliseString.extrairNumeros(ateMovimento.getPessoa().getDocumento()))) {
////                    setMsgCPF("CPF inválido!");
//                    GenericaMensagem.warn("Atenção", "CPF inválido!");
//                    return ;
//                } else {
//  //                  setMsgCPF("");
//                }
//            } else {
//                //setMsgCPF("");
//                return;
//            }
//            valorPesquisa = ateMovimento.getPessoa().getDocumento();
//        } else if (tipoVerificacao.equals("rg")) {
//            if (ateMovimento.getPessoa().getRg().isEmpty()) {
//                return;
//            }
//            if (ateMovimento.getPessoa().getId() != -1) {
//                return;
//            }
//            valorPesquisa = ateMovimento.getPessoa().getRg();
//        }
//        
//        PessoaDB db = new PessoaDBToplink();
//        pessoa = (Pessoa) db.pessoaDocumento(valorPesquisa);
//        if (pessoa != null) {
//            ateMovimento.getPessoa().setNome(pessoa.getNome());
//            ateMovimento.getPessoa().setDocumento(pessoa.getDocumento());
//            ateMovimento.getPessoa().setTelefone(pessoa.getTelefone1());
//            if (!ateMovimento.getPessoa().getTelefone().equals("(__) ____-____")) {
//                ateMovimento.getPessoa().setDocumento(pessoa.getDocumento());
//            }
//            FisicaDB fisicaDB = new FisicaDBToplink();
//            fisica = (Fisica) fisicaDB.pesquisaFisicaPorPessoa(pessoa.getId());
//            ateMovimento.getPessoa().setRg(fisica.getRg());
//            ateMovimento.getPessoa().setTelefone(pessoa.getTelefone1());
////            if (fisica.getRg().equals("") || pessoa.getDocumento().equals("") || pessoa.getTelefone1().equals("")) {
////                setEditaPessoa(false);
////            }
//            //setMsgCPF("");
////            setDesabilitaCamposPessoa(true);
//            PessoaEmpresaDB pedb = new PessoaEmpresaDBToplink();
//
//            PessoaEmpresa pe = pedb.pesquisaPessoaEmpresaPorFisica(fisica.getId());
//
//            if (pe.getId() != -1){
//                empresa = pe.getJuridica();
//            }
//        } else {
//            AtendimentoDao atendimentoDB = new AtendimentoDao();
//            SisPessoa atePessoaB = (SisPessoa) atendimentoDB.pessoaDocumento(valorPesquisa);
//            //setMsgCPF("");
//            if (ateMovimento == null || (atePessoaB == null || atePessoaB.getId() == -1)) {
////                    AtePessoa atePes = new AtePessoa();
////                    ateMovimento.setPessoa(atePes);
//                //setEditaPessoa(false);
//            } else {
//                ateMovimento.setPessoa(atePessoaB);
////                setEditaPessoa(false);
////                setDesabilitaCamposPessoa(true);
//            }
//        }
//    }

    public List<SelectItem> getListaFiliais() {
        if (listaFiliais.isEmpty()) {
            Dao dao = new Dao();
            List<Filial> listaFilial = (List<Filial>) dao.list(new Filial(), true);
            for (int i = 0; i < listaFilial.size(); i++) {
                listaFiliais.add(new SelectItem(i,
                        listaFilial.get(i).getFilial().getPessoa().getDocumento() + " / " + listaFilial.get(i).getFilial().getPessoa().getNome(),
                        Integer.toString(listaFilial.get(i).getId())));
            }
        }
        return listaFiliais;
    }
    
    public void setListaFiliais(List<SelectItem> listaFiliais) {
        this.listaFiliais = listaFiliais;
    }
    
    public List<SelectItem> getListaAtendimentoOperacoes() {
        if (listaAtendimentoOperacoes.isEmpty()) {
            Dao dao = new Dao();
            List<AteOperacao> list = dao.list(new AteOperacao(), true);
            if (list != null) {
                int i = 0;
                while (i < list.size()) {
                    listaAtendimentoOperacoes.add(new SelectItem(new Integer(i), list.get(i).getDescricao(), Integer.toString(list.get(i).getId())));
                    i++;
                }
            }
            
        }
        return listaAtendimentoOperacoes;
    }
    
    public AteOperacao getAteOperacao() {
        return ateOperacao;
    }
    
    public void setAteOperacao(AteOperacao ateOperacao) {
        this.ateOperacao = ateOperacao;
    }
    
    public AteMovimento getAteMovimento() {
        if (FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("acessoFilial") != null) {
            if (filial.getId() == -1) {
                macFilial = (MacFilial) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("acessoFilial");
                Dao dao = new Dao();
                filial = (Filial) dao.find(new Filial(), macFilial.getFilial().getId());
                ateMovimento.setFilial(filial);
            }
        }
        return ateMovimento;
    }
    
    public void setAteMovimento(AteMovimento ateMovimento) {
        this.ateMovimento = ateMovimento;
    }
    
    public SisPessoa getAtePessoa() {
        return atePessoa;
    }
    
    public void setAtePessoa(SisPessoa atePessoa) {
        this.atePessoa = atePessoa;
    }
    
    public int getIdIndexPessoa() {
        return idIndexPessoa;
    }
    
    public void setIdIndexPessoa(int idIndexPessoa) {
        this.idIndexPessoa = idIndexPessoa;
    }
    
    public int getIdIndexMovimento() {
        return idIndexMovimento;
    }
    
    public void setIdIndexMovimento(int idIndexMovimento) {
        this.idIndexMovimento = idIndexMovimento;
    }
    
    public List<AteMovimento> getListaAteMovimento() {
        if (!sisPessoa.getDocumento().equals("___.___.___-__")) {
            listaAteMovimento.clear();
            AtendimentoDao db = new AtendimentoDao();
            if (!sisPessoa.getDocumento().isEmpty()) {
                listaAteMovimento = db.listaAteMovimentos(sisPessoa.getDocumento(), porPesquisa, filial.getId());
            } else {
                listaAteMovimento = db.listaAteMovimentos("", porPesquisa, filial.getId());
            }
            
        }
        
        return listaAteMovimento;
    }
    
    public void setListaAteMovimento(List<AteMovimento> listaAteMovimento) {
        this.listaAteMovimento = listaAteMovimento;
    }
    
    public List<SisPessoa> getListaAtePessoas() {
        return listaAtePessoas;
    }
    
    public void setListaAtePessoas(List<SisPessoa> listaAtePessoas) {
        this.listaAtePessoas = listaAtePessoas;
    }
    
    public int getIdFilial() {
        return idFilial;
    }
    
    public void setIdFilial(int idFilial) {
        this.idFilial = idFilial;
    }
    
    public int getIdOperacao() {
        return idOperacao;
    }
    
    public void setIdOperacao(int idOperacao) {
        this.idOperacao = idOperacao;
    }
    
    public String getPorPesquisa() {
        return porPesquisa;
    }
    
    public void setPorPesquisa(String porPesquisa) {
        this.porPesquisa = porPesquisa;
    }
    
    public String getHoraEmissaoString() {
        if (!this.horaEmissaoString.equals("")) {
            return this.horaEmissaoString;
        } else {
            Date date = new Date();
            return DataHoje.livre(date, "HH:mm");
        }
    }
    
    public void setHoraEmissaoString(String horaEmissaoString) {
        this.horaEmissaoString = horaEmissaoString;
    }
    
    public MacFilial getMacFilial() {
        return macFilial;
    }
    
    public void setMacFilial(MacFilial macFilial) {
        this.macFilial = macFilial;
    }
    
    public Filial getFilial(Filial filial) {
        return filial;
    }
    
    public void setFilial(Filial filial) {
        this.filial = filial;
    }
    
    public Juridica getEmpresa() {
        if (GenericaSessao.getObject("juridicaPesquisa") != null) {
            empresa = (Juridica) GenericaSessao.getObject("juridicaPesquisa");
            GenericaSessao.remove("juridicaPesquisa");
        }
        return empresa;
    }
    
    public void setEmpresa(Juridica empresa) {
        this.empresa = empresa;
    }
    
    public SisPessoa getSisPessoaAtualiza() {
        return sisPessoaAtualiza;
    }
    
    public void setSisPessoaAtualiza(SisPessoa sisPessoaAtualiza) {
        this.sisPessoaAtualiza = sisPessoaAtualiza;
    }
    
    public StreamedContent getFileDownload() {
        return fileDownload;
    }
    
    public void setFileDownload(StreamedContent fileDownload) {
        this.fileDownload = fileDownload;
    }
    
    public boolean isVisibleModal() {
        return visibleModal;
    }
    
    public void setVisibleModal(boolean visibleModal) {
        this.visibleModal = visibleModal;
    }
    
    public String getTipoTelefone() {
        return tipoTelefone;
    }
    
    public void setTipoTelefone(String tipoTelefone) {
        this.tipoTelefone = tipoTelefone;
    }
    
    public List<SelectItem> getListaUsuarios() {
        if (listaUsuarios.isEmpty()) {
            PermissaoUsuarioDao pud = new PermissaoUsuarioDao();
//            Permissao permissao = db.pesquisaPermissao(4, 114, 4);

//            AtendimentoDao db = new AtendimentoDao();
            // DEPARTAMENTO 8 - HOMOLOGAÇÃO ---
            List<PermissaoUsuario> result = pud.listaPermissaoUsuarioDepartamento(8);
            
            if (result.isEmpty()) {
                listaUsuarios.add(new SelectItem(0, "Nenhum Usuário Encontrado", "0"));
                return listaUsuarios;
            }
            
            for (int i = 0; i < result.size(); i++) {
                listaUsuarios.add(new SelectItem(
                        i,
                        result.get(i).getUsuario().getPessoa().getNome(),
                        Integer.toString(result.get(i).getId()))
                );
            }
        }
        return listaUsuarios;
    }
    
    public void setListaUsuarios(List<SelectItem> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
    }
    
    public int getIndex_usuario() {
        return index_usuario;
    }
    
    public void setIndex_usuario(int index_usuario) {
        this.index_usuario = index_usuario;
    }
    
    public boolean isChkReserva() {
        return chkReserva;
    }
    
    public void setChkReserva(boolean chkReserva) {
        this.chkReserva = chkReserva;
    }
    
}
