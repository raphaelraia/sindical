package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.HistoricoCarteirinha;
import br.com.rtools.associativo.ModeloCarteirinha;
import br.com.rtools.associativo.ModeloCarteirinhaCategoria;
import br.com.rtools.associativo.SocioCarteirinha;
import br.com.rtools.associativo.Socios;
import br.com.rtools.associativo.dao.ModeloCarteirinhaCategoriaDao;
import br.com.rtools.associativo.dao.SocioCarteirinhaDao;
import br.com.rtools.associativo.dao.SociosDao;
import br.com.rtools.financeiro.CondicaoPagamento;
import br.com.rtools.financeiro.FStatus;
import br.com.rtools.financeiro.FTipoDocumento;
import br.com.rtools.financeiro.Lote;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.financeiro.TipoRecibo;
import br.com.rtools.financeiro.TipoServico;
import br.com.rtools.financeiro.beans.BaixaGeralBean;
import br.com.rtools.financeiro.dao.MovimentoDao;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.dao.FisicaDao;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.controleUsuario.ChamadaPaginaBean;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.ImageConverter;
import br.com.rtools.utilitarios.Sessions;
import br.com.rtools.utilitarios.dao.FunctionsDao;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import org.primefaces.model.StreamedContent;

@ManagedBean
@SessionScoped
public class GeracaoDebitosCartaoBean implements Serializable {

    private Fisica fisica;
    private List<Socios> listaSocios;
    private List<Socios> selected;
    private Lote lote;
    private Registro registro;
    private List<Movimento> listMovimentos;
    private Boolean habilitaImpressao;
    private List<HistoricoCarteirinha> listHistoricoCarteirinhas;

    // FOTO
//    private StreamedContent fotoStreamed = null;
//    private String nomeFoto = "";    
    @PostConstruct
    public void init() {
        fisica = new Fisica();
        listaSocios = new ArrayList<>();
        selected = null;
        lote = new Lote();
        registro = (Registro) new Dao().find(new Registro(), 1);
        listMovimentos = new ArrayList();
        listHistoricoCarteirinhas = new ArrayList();
        habilitaImpressao = false;

//        PhotoCapture.load("temp/foto/" + Usuario.getUsuario().getId(), "form_gdc");
//        PhotoUpload.load("temp/foto/" + Usuario.getUsuario().getId(), "form_gdc");
//        fotoStreamed = null;
//        nomeFoto = "";
    }

    @PreDestroy
    public void destroy() {
        Sessions.remove("photoCapture");
        Sessions.remove("geracaoDebitosCartaoBean");
        Sessions.remove("cartaoSocialBean");
        Sessions.remove("fisicaPesquisa");
        Sessions.remove("fisicaBean");
        Sessions.remove("baixa_sucesso");
        Sessions.remove("cartao_social_sucesso");
        Sessions.remove("listaMovimento");
        Sessions.remove("lista_movimentos_baixados");
        Sessions.remove("pessoaUtilitariosBean");
        clear(2);
    }

    public StreamedContent getFotoStreamed() {
        String id_pessoa = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("image_pessoa_id");
//        
////        FacesContext context = FacesContext.getCurrentInstance();
////        HttpServletRequest myRequest = (HttpServletRequest)context.getExternalContext().getRequest();
////        HttpSession mySession = myRequest.getSession();        
////        String id_pessoa = myRequest.getParameter("image_pessoa_id");
//        //String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("image_id");
        if (id_pessoa == null) {
            return null;
        }
        FisicaDao fisicaDB = new FisicaDao();
        Fisica fisica_imagem = fisicaDB.pesquisaFisicaPorPessoa(Integer.valueOf(28502));

        File file = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/Fotos/Fisica/" + fisica_imagem.getFoto() + ".png"));
        if (file.exists()) {
            return ImageConverter.getImageStreamed(file, "image/png");
        }

        file = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/Fotos/Fisica/" + fisica_imagem.getFoto() + ".jpg"));
        if (file.exists()) {
            return ImageConverter.getImageStreamed(file, "image/png");
        }

        StreamedContent sc;
        if (fisica_imagem.getSexo().equals("F")) {
            file = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Imagens/user_female.png"));
            sc = ImageConverter.getImageStreamed(file, "image/png");
        } else {
            file = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Imagens/user_male.png"));
            sc = ImageConverter.getImageStreamed(file, "image/png");
        }
        return sc;
    }

    public void load() {
        if (GenericaSessao.exists("baixa_sucesso")) {
            if (GenericaSessao.exists("lista_movimentos_baixados")) {
                List<Movimento> list = GenericaSessao.getList("lista_movimentos_baixados", true);
                listHistoricoCarteirinhas.clear();
                if (list.isEmpty()) {
                    selected = null;
                    listMovimentos.clear();
                    GenericaMensagem.warn("Erro", "Erro ao emitir cartões");
                    return;
                }
                listHistoricoCarteirinhas = CartaoSocialBean.gerarHistoricoCarteirinhas(list, getListaSocios().get(0).getMatriculaSocios().getCategoria().getId(), 170);
                listMovimentos.clear();
                GenericaSessao.remove("baixa_sucesso");
                if (!listHistoricoCarteirinhas.isEmpty()) {
                    for (int i = 0; i < listHistoricoCarteirinhas.size(); i++) {
                        listHistoricoCarteirinhas.get(i).getCarteirinha().setEmissao(DataHoje.data());
                        new Dao().update(listHistoricoCarteirinhas.get(i).getCarteirinha(), true);
                    }
                    GenericaMensagem.info("Sucesso", "Operação realizada com sucesso. Você já pode emitir seus cartões.");
                    return;
                }
            }
        }
        if (GenericaSessao.exists("cartao_social_sucesso")) {
            GenericaSessao.remove("cartao_social_sucesso");
            listHistoricoCarteirinhas.clear();
            selected = null;
            GenericaMensagem.info("Sucesso", "Cartão impresso com sucesso!");
        }
    }

    public String save() {
        if (fisica.getPessoa().getId() == -1) {
            GenericaMensagem.warn("Validação", "Pesquise uma pessoa para gerar!");
            return null;
        }
        if (selected.isEmpty()) {
            GenericaMensagem.warn("Validação", "Selecione pelo menos um item da lista!");
            return null;
        }
        if (registro.getServicos() == null) {
            GenericaMensagem.warn("Erro", "INFORMAR SERVIÇO DO CARTÃO SOCIAL (ID) NO REGISTRO EMPRESARIAL");
        }
        Dao dao = new Dao();
        Servicos serv = (Servicos) registro.getServicos();

        // CODICAO DE PAGAMENTO
        CondicaoPagamento cp = (CondicaoPagamento) dao.find(new CondicaoPagamento(), 1);
        // TIPO DE DOCUMENTO  FTipo_documento 13 - CARTEIRA, 2 - BOLETO
        FunctionsDao functionsDao = new FunctionsDao();
        FTipoDocumento td = (FTipoDocumento) dao.find(new FTipoDocumento(), 2);
        lote = new Lote();
        lote.setEmissao(DataHoje.data());
        lote.setPagRec("R");
        lote.setValor(functionsDao.valorServico(fisica.getPessoa().getId(), serv.getId(), DataHoje.dataHoje(), 0, null));
        lote.setFilial(serv.getFilial());
        lote.setEvt(null);
        lote.setPessoa(fisica.getPessoa());
        lote.setFTipoDocumento(td);
        lote.setRotina((Rotina) dao.find(new Rotina(), 301));
        lote.setStatus((FStatus) dao.find(new FStatus(), 1));
        lote.setPessoaSemCadastro(null);
        lote.setDepartamento(serv.getDepartamento());
        lote.setCondicaoPagamento(cp);
        lote.setPlano5(serv.getPlano5());

        dao.openTransaction();
        if (!dao.save(lote)) {
            GenericaMensagem.warn("Erro", "Ao salvar Lote!");
            dao.rollback();
            return null;
        }

        TipoServico tipoServico = (TipoServico) dao.find(new TipoServico(), 1);

        Movimento movimento;
        for (int i = 0; i < selected.size(); i++) {
            double valor = functionsDao.valorServico(selected.get(i).getServicoPessoa().getPessoa().getId(), serv.getId(), DataHoje.dataHoje(), 0, null);
            movimento = new Movimento();
            movimento.setLote(lote);
            movimento.setValor(valor);
            movimento.setValorBaixa(valor);
            movimento.setBaixa(null);
            movimento.setAcordo(null);
            movimento.setTitular(selected.get(i).getMatriculaSocios().getTitular());
            movimento.setPessoa(selected.get(i).getMatriculaSocios().getTitular());
            movimento.setBeneficiario(selected.get(i).getServicoPessoa().getPessoa());
            movimento.setFTipoDocumento(td);
            movimento.setMatriculaSocios(selected.get(i).getMatriculaSocios());
            movimento.setVencimento(DataHoje.data());
            movimento.setReferencia(DataHoje.converteDataParaReferencia(DataHoje.data()));
            movimento.setAtivo(true);
            movimento.setEs("E");
            movimento.setTipoServico(tipoServico);
            movimento.setPlano5(lote.getPlano5());
            movimento.setServicos(serv);
            if (dao.save(movimento)) {
                listMovimentos.add(movimento);
            } else {
                dao.rollback();
                lote = new Lote();
                return null;
            }
            ModeloCarteirinhaCategoria mcc = new ModeloCarteirinhaCategoriaDao().findByModeloCarteirinha(170, selected.get(i).getMatriculaSocios().getCategoria().getId());
            if (mcc == null) {
                GenericaMensagem.warn("Erro", "Modelo carteirinha não encontrado!");
                dao.rollback();
                lote = new Lote();
                return null;
            }
            SocioCarteirinha socioCarteirinha = new SocioCarteirinhaDao().pesquisaCarteirinhaPessoa(selected.get(i).getServicoPessoa().getPessoa().getId(), mcc.getModeloCarteirinha().getId());
            if (socioCarteirinha == null) {
                GenericaMensagem.warn("Erro", "Nenhuma carteirinha encontrada!");
                dao.rollback();
                lote = new Lote();
                return null;
            } else {
                socioCarteirinha.setDtEmissao(null);
                if (!dao.update(socioCarteirinha)) {
                    GenericaMensagem.warn("Erro", "Ao atualizar carteirinha!");
                    dao.rollback();
                    lote = new Lote();
                    return null;
                }
            }
        }

        dao.commit();

        List<Movimento> listMovimentoSelected = new ArrayList();
        for (int i = 0; i < selected.size(); i++) {
            for (int x = 0; x < listMovimentos.size(); x++) {
                if (selected.get(i).getServicoPessoa().getPessoa().getId() == listMovimentos.get(x).getBeneficiario().getId()) {
                    listMovimentos.set(x, ((Movimento) dao.find(listMovimentos.get(x))));
                    if (listMovimentos.get(x).getBaixa() == null) {
                        listMovimentoSelected.add(listMovimentos.get(x));
                        break;
                    }
                }
            }
        }
        listaSocios.clear();
        BaixaGeralBean.listenerTipoCaixaSession("caixa");
        GenericaSessao.put("listaMovimento", listMovimentoSelected);
        GenericaMensagem.info("Sucesso", "Geração efetuada com sucesso!");
        GenericaSessao.put("tipo_recibo_imprimir", new Dao().find(new TipoRecibo(), 1));
        return ((ChamadaPaginaBean) GenericaSessao.getObject("chamadaPaginaBean")).baixaGeral();
        // return null;
    }

    public void clear(Integer tCase) {
        // Limpa toda sessão
        if (tCase == 0) {
            GenericaSessao.remove("geracaoDebitosCartaoBean");
            clear(2);
        }
        // Limpar e manter Sócio (Física)
        if (tCase == 1) {
            listaSocios.clear();
            selected = null;
            lote = new Lote();
            listMovimentos = new ArrayList();
            listHistoricoCarteirinhas.clear();
            habilitaImpressao = false;
            clear(2);
        }
        if (tCase == 2) {
//            try {
//                FileUtils.deleteDirectory(new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("") + "/Cliente/" + ControleUsuarioBean.getCliente() + "/temp/" + "foto/" + new SegurancaUtilitariosBean().getSessaoUsuario().getId()));
//                File f = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/Fotos/" + -1 + ".png"));
//                if (f.exists()) {
//                    f.delete();
//                }
//            } catch (IOException ex) {
//
//            }
        }
        if (tCase == 3) {
//            GenericaSessao.remove("cropperBean");
//            GenericaSessao.remove("uploadBean");
//            GenericaSessao.remove("photoCamBean");
        }
    }

    public Fisica getFisica() {
        if (GenericaSessao.exists("fisicaPesquisa")) {
            fisica = (Fisica) GenericaSessao.getObject("fisicaPesquisa", true);
            listaSocios.clear();
            listMovimentos.clear();
            listHistoricoCarteirinhas.clear();
            selected = null;
            habilitaImpressao = false;
            GenericaSessao.remove("baixa_sucesso");
            GenericaSessao.remove("lista_movimentos_baixados");
            GenericaSessao.remove("listaMovimento");
            GenericaSessao.remove("cartaoSocialBean");
            GenericaSessao.remove("pessoaUtilitariosBean");
            clear(2);
        }
        return fisica;
    }

    public void setFisica(Fisica fisica) {
        this.fisica = fisica;
    }

    public List<Socios> getListaSocios() {
        if (listaSocios.isEmpty() && fisica.getPessoa().getId() != -1 && registro.getServicos() != null) {
            SociosDao sociosDB = new SociosDao();
            Socios s = sociosDB.pesquisaSocioPorPessoaAtivo(fisica.getPessoa().getId());
            if (s != null) {
                listaSocios = sociosDB.pesquisaDependentePorMatricula(s.getMatriculaSocios().getId(), false);
                for (int i = 0; i < listaSocios.size(); i++) {
                    // Campo opcional
                    listaSocios.get(i).setObject(getMovimento(listaSocios.get(i).getServicoPessoa().getPessoa()));
                }
            }
        }
        return listaSocios;
    }

    public void setListaSocios(List<Socios> listaSocios) {
        this.listaSocios = listaSocios;
    }

    public Fisica pessoaFisica(Pessoa p) {
        Fisica f = new FisicaDao().pesquisaFisicaPorPessoa(p.getId());
        return f;
    }

    public List<Socios> getSelected() {
        return selected;
    }

    public void setSelected(List<Socios> selected) {
        this.selected = selected;
    }

    public Movimento getMovimento(Pessoa p) {
        MovimentoDao mdb = new MovimentoDao();
        mdb.setLimit(1);
        List list = mdb.listaMovimentosUltimosDias(p.getId(), registro.getServicos().getId(), 301, 10, 2);
        if (!list.isEmpty()) {
            return (Movimento) list.get(0);
        }
        return null;
    }

    public Registro getRegistro() {
        return registro;
    }

    public void setRegistro(Registro registro) {
        this.registro = registro;
    }

    public Boolean renderedUpload(Pessoa p) {
        ModeloCarteirinha modeloc = new SocioCarteirinhaDao().pesquisaModeloCarteirinha(p.getSocios().getMatriculaSocios().getCategoria().getId(), 170);
        if (modeloc == null) {
            GenericaMensagem.warn("Atenção", "Sócio sem modelo de Carteirinha!");
            return false;
        }
        if (modeloc.getFotoCartao()) {
            if (!p.getFisica().getFoto().isEmpty()) {
                return false;
            }
        } else {
            return true;
        }
        return false;
    }

    public Boolean disabled(Pessoa p, Movimento m) {
        ModeloCarteirinha modeloc = new SocioCarteirinhaDao().pesquisaModeloCarteirinha(p.getSocios().getMatriculaSocios().getCategoria().getId(), 170);
        if (modeloc == null) {
            GenericaMensagem.warn("Atenção", "Sócio sem modelo de Carteirinha!");
            return false;
        }
        if (modeloc.getFotoCartao()) {
            if (p.getFisica().getFoto() == null || p.getFisica().getFoto().isEmpty()) {
                return true;
            } else if (m != null) {
                return true;
            }
        } else {
            return m != null;
        }
        return false;
    }

    public Boolean getHabilitaImpressao() {
        return habilitaImpressao;
    }

    public void setHabilitaImpressao(Boolean habilitaImpressao) {
        this.habilitaImpressao = habilitaImpressao;
    }

    public List<HistoricoCarteirinha> getListHistoricoCarteirinhas() {
        return listHistoricoCarteirinhas;
    }

    public void setListHistoricoCarteirinhas(List<HistoricoCarteirinha> listHistoricoCarteirinhas) {
        this.listHistoricoCarteirinhas = listHistoricoCarteirinhas;
    }

    public void sendPrint() {
        String inPessoasImprimir = "";
        for (int i = 0; i < listaSocios.size(); i++) {
            if (disabled(listaSocios.get(i).getServicoPessoa().getPessoa(), (Movimento) listaSocios.get(i).getObject())) {
                if (inPessoasImprimir.isEmpty()) {
                    inPessoasImprimir = "" + listaSocios.get(i).getServicoPessoa().getPessoa().getId();
                } else {
                    inPessoasImprimir += "," + listaSocios.get(i).getServicoPessoa().getPessoa().getId();
                }
            }
        }
        if (!inPessoasImprimir.isEmpty()) {
            GenericaSessao.put("inPessoasImprimir", inPessoasImprimir);
        }
        selected = null;
    }

//    public StreamedContent getFotoStreamed() {
//        try {
//            if (PhotoCapture.getFile() != null) {
//                nomeFoto = PhotoCapture.getImageName();
//                PhotoCapture.unload();
//            }
//            
//            if (PhotoUpload.getFile() != null) {
//                nomeFoto = PhotoUpload.getImageName();
//                PhotoUpload.unload();
//            }
//
//            fotoStreamed = null;
//            File fotoTempx = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/temp/foto/" + Usuario.getUsuario().getId() + "/" + nomeFoto + ".png"));
//            if (fotoTempx.exists()) {
//                fotoStreamed = ImageConverter.getImageStreamed(fotoTempx, "image/png");
//            } else {
//                File fotoSave = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/Fotos/Fisica/" + fisica.getFoto() + ".png"));
//                if (fotoSave.exists()) {
//                    fotoStreamed = ImageConverter.getImageStreamed(fotoSave, "image/png");
//                }
//                
//            }
//        } catch (Exception e) {
//            e.getMessage();
//        }
//        return fotoStreamed;
//    }
//
//    public void setFotoStreamed(StreamedContent fotoStreamed) {
//        this.fotoStreamed = fotoStreamed;
//    }
//
//    public String getNomeFoto() {
//        return nomeFoto;
//    }
//
//    public void setNomeFoto(String nomeFoto) {
//        this.nomeFoto = nomeFoto;
//    }    
}
