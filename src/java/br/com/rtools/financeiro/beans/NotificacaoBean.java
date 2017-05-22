package br.com.rtools.financeiro.beans;

import br.com.rtools.arrecadacao.ConfiguracaoArrecadacao;
import br.com.rtools.arrecadacao.dao.GrupoCidadesDao;
import br.com.rtools.endereco.Cidade;
import br.com.rtools.financeiro.CobrancaEnvio;
import br.com.rtools.financeiro.CobrancaLote;
import br.com.rtools.financeiro.CobrancaTipo;
import br.com.rtools.financeiro.PollingEmail;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.financeiro.TipoServico;
import br.com.rtools.financeiro.dao.NotificacaoDao;
import br.com.rtools.financeiro.dao.ServicoRotinaDao;
import br.com.rtools.impressao.ParametroEtiqueta;
import br.com.rtools.impressao.ParametroNotificacao;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.PessoaEndereco;
import br.com.rtools.pessoa.dao.PessoaEnderecoDao;
import br.com.rtools.pessoa.dao.JuridicaDao;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.sistema.Email;
import br.com.rtools.sistema.EmailPessoa;
import br.com.rtools.sistema.Links;
import br.com.rtools.sistema.dao.LinksDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DaoInterface;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.DataObject;
import br.com.rtools.utilitarios.Download;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Mail;
import br.com.rtools.utilitarios.SalvaArquivos;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;
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
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;

@ManagedBean
@SessionScoped
public class NotificacaoBean implements Serializable {

    private int idLista = 0;
    private int idTipoEnvio = 0;
    private List<SelectItem> itensLista = new ArrayList();
    private List<SelectItem> listaTipoEnvio = new ArrayList();
    private CobrancaLote lote = new CobrancaLote();
    private List<DataObject> listaNotificacao = new ArrayList();
    private int quantidade = 0;
    private int quantidadeMenu = 0;
    private int indexTab = 0;
    private boolean chkTodos = true;
    private boolean empresa = true;
    private boolean habilitaNot = false;
    private List<DataObject> listaEmpresaAdd = new ArrayList();
    private List<DataObject> listaContabilAdd = new ArrayList();
    private Registro registro = null;
    private final List<DataObject> listaCidadesBase = new ArrayList();
    private boolean chkCidadesBase = false;
    private boolean comContabil = false;
    private boolean semContabil = false;
    private String tabAtiva = "todos";
    private String query = "";
    private int valorAtual = 0;
    private boolean progressoAtivo = false;
    private List<DataObject> listaArquivo = new ArrayList();

    private Boolean chkServicos = false;
    private List<ListaDeServicos> listaServicos = new ArrayList();

    private Boolean chkTipoServico = false;
    private List<ListaDeTipoServico> listaTipoServico = new ArrayList();
    private ConfiguracaoArrecadacao ca = new ConfiguracaoArrecadacao();

    private String tipoEmpresa = "ativas";

    private Boolean chkImprimirVerso = false;

    public NotificacaoBean() {
        registro = (Registro) new Dao().find(new Registro(), 1);
        ca = (ConfiguracaoArrecadacao) new Dao().find(new ConfiguracaoArrecadacao(), 1);
    }

    public void loadListaArquivos() {
        listaArquivo.clear();

        LinksDao db = new LinksDao();

        try {
            String caminho = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/notificacao/" + lote.getId());
            File files = new File(caminho);
            File listFile[] = files.listFiles();

            for (int i = 0; i < listFile.length; i++) {
                Links link = db.pesquisaNomeArquivo(listFile[i].getName());
                if (link == null) {
                    continue;
                }

                listaArquivo.add(new DataObject(registro.getUrlPath() + "/Sindical/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/notificacao/" + lote.getId() + "/" + listFile[i].getName(), i + 1, link));
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void acoes() {
        if (indexTab == 0) {
            gerarParaTodasAtivas();
        }

        if (indexTab == 1) {
            gerarParaTodasInativas();
        }

        if (indexTab == 2) {
            gerarParaTodas();
        }

        if (indexTab == 3) {
            addEmpresa();
        }

        if (indexTab == 4) {
            addContabil();
        }

        if (indexTab == 5) {
            gerarSemContabil();
        }

        if (indexTab == 5) {
            gerarComContabil();
        }
    }

    public void removerListaJuridica() {
        listaEmpresaAdd.clear();
        listaContabilAdd.clear();
        if (indexTab == 3) {
            addEmpresa();
        }

        if (indexTab == 4) {
            addContabil();
        }
    }

    public void iniciar() {
        progressoAtivo = true;
        valorAtual = 0;
    }

    public void increment() {
        if (valorAtual < 100) {
            valorAtual += 2;
            if (valorAtual >= 100) {
                progressoAtivo = false;
            }
        }
    }

    public void gerarParaTodasAtivas() {
        tipoEmpresa = "ativas";
        listaNotificacao.clear();
        listaEmpresaAdd.clear();
        listaContabilAdd.clear();
        comContabil = false;
        semContabil = false;
    }

    public void gerarParaTodasInativas() {
        tipoEmpresa = "inativas";
        listaNotificacao.clear();
        listaEmpresaAdd.clear();
        listaContabilAdd.clear();
        comContabil = false;
        semContabil = false;
    }

    public void gerarParaTodas() {
        tipoEmpresa = "todas";
        listaNotificacao.clear();
        listaEmpresaAdd.clear();
        listaContabilAdd.clear();
        comContabil = false;
        semContabil = false;
    }

    public void gerarComContabil() {
        tipoEmpresa = "ativas";
        listaNotificacao.clear();
        listaEmpresaAdd.clear();
        listaContabilAdd.clear();
        comContabil = true;
        semContabil = false;
    }

    public void gerarSemContabil() {
        tipoEmpresa = "ativas";
        listaNotificacao.clear();
        listaEmpresaAdd.clear();
        listaContabilAdd.clear();
        comContabil = false;
        semContabil = true;
    }

    public void addEmpresa() {
        tipoEmpresa = "ativas";
        listaNotificacao.clear();
        empresa = true;
        comContabil = false;
        semContabil = false;
        listaContabilAdd.clear();
    }

    public void addContabil() {
        tipoEmpresa = "ativas";
        listaNotificacao.clear();
        empresa = false;
        comContabil = false;
        semContabil = false;
        listaEmpresaAdd.clear();
    }

    public void addCidades() {
        tipoEmpresa = "ativas";
        listaNotificacao.clear();
        comContabil = false;
        semContabil = false;
    }

    public void addServicos() {
        tipoEmpresa = "ativas";
        listaNotificacao.clear();
        comContabil = false;
        semContabil = false;
    }

    public void addTipoServico() {
        tipoEmpresa = "ativas";
        listaNotificacao.clear();
        comContabil = false;
        semContabil = false;
    }

    public synchronized List<DataObject> getListaNotificacao() {
        if (listaNotificacao.isEmpty()) {
            NotificacaoDao db = new NotificacaoDao();
            //quantidade = 0;
            String empresas = "", contabils = "", cidades = "", servicos = "", tipo_servico = "";
            for (int i = 0; i < listaEmpresaAdd.size(); i++) {
                if (empresas.length() > 0 && i != listaEmpresaAdd.size()) {
                    empresas += ",";
                }
                empresas += listaEmpresaAdd.get(i).getArgumento0();
            }

            for (int i = 0; i < listaContabilAdd.size(); i++) {
                if (contabils.length() > 0 && i != listaContabilAdd.size()) {
                    contabils += ",";
                }
                contabils += listaContabilAdd.get(i).getArgumento0();
            }

            for (int i = 0; i < listaCidadesBase.size(); i++) {
                if ((Boolean) listaCidadesBase.get(i).getArgumento0()) {
                    if (cidades.length() > 0 && i != listaCidadesBase.size()) {
                        cidades += ",";
                    }
                    cidades += ((Cidade) listaCidadesBase.get(i).getArgumento1()).getId();
                }
            }

            for (int i = 0; i < listaServicos.size(); i++) {
                if (listaServicos.get(i).getChk()) {
                    if (servicos.length() > 0 && i != listaServicos.size()) {
                        servicos += ",";
                    }
                    servicos += listaServicos.get(i).getServicos().getId();
                }
            }

            for (int i = 0; i < listaTipoServico.size(); i++) {
                if (listaTipoServico.get(i).getChk()) {
                    if (tipo_servico.length() > 0 && i != listaTipoServico.size()) {
                        tipo_servico += ",";
                    }
                    tipo_servico += listaTipoServico.get(i).getTipoServico().getId();
                }
            }

            List<Vector> result = null;
            Object[] obj = new Object[2];

            if (lote.getId() != -1) {
                obj = db.listaParaNotificacao(lote.getId(), DataHoje.data(), empresas, contabils, cidades, comContabil, semContabil, servicos, tipo_servico, tipoEmpresa);
            } else // EMPRESA --
             if ((indexTab == 3 && empresas.isEmpty()) || (indexTab == 4 && contabils.isEmpty())) {
                    return listaNotificacao;
                } else {
                    obj = db.listaParaNotificacao(-1, DataHoje.data(), empresas, contabils, cidades, comContabil, semContabil, servicos, tipo_servico, tipoEmpresa);
                }

            result = (Vector) obj[1];
            if (!result.isEmpty()) {
                query = String.valueOf(obj[0]);
                for (int i = 0; i < result.size(); i++) {
                    String noti = (result.get(i).get(5) == null) ? "Nunca" : DataHoje.converteData((Date) result.get(i).get(5));
                    listaNotificacao.add(new DataObject(true, result.get(i), noti, null, null, null));
                }
            }
        }
        if (listaNotificacao.isEmpty()) {
            query = "";
        }
        return listaNotificacao;
    }

    public String salvar() {
        Dao dao = new Dao();
        dao.openTransaction();

        if (!dao.update(lote)) {
            GenericaMensagem.warn("Erro", "Erro ao atualizar Mensagem");
            dao.rollback();
            return null;
        }

        LinksDao db = new LinksDao();
        try {
            String caminho = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/notificacao/" + lote.getId());
            File files = new File(caminho);
            File listFile[] = files.listFiles();

            if (listFile.length > 0) {
                for (int i = 0; i < listFile.length; i++) {
                    Links link = db.pesquisaNomeArquivo(listFile[i].getName());
                    if (link == null) {
                        continue;
                    }

                    if (dao.delete(link)) {
                        File f_delete = new File(caminho + "/" + listFile[i].getName());
                        if (f_delete.exists()) {
                            f_delete.delete();
                        }
                    }

                }
                loadListaArquivos();
            }
        } catch (Exception e) {
            //sv.desfazerTransacao();
            //return null;
        }

        dao.commit();
        GenericaMensagem.info("Sucesso", "Notificação atualizada!");
        return null;
    }

    public String gerarNotificacao() {
        if (((Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("sessaoUsuario")).getId() == -1) {
            GenericaMensagem.warn("Erro", "Usuário não esta na sessão, faça seu login novamente!");
            return null;
        }

        if (lote.getId() != -1) {
            lote = new CobrancaLote();
        }
        lote.setUsuario(((Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("sessaoUsuario")));
        lote.setDtEmissao(DataHoje.dataHoje());
        lote.setHora(DataHoje.horaMinuto());

        NotificacaoDao db = new NotificacaoDao();
//        if (db.pesquisaCobrancaLote(lote.getUsuario().getId(), lote.getDtEmissao()) != null) {
//            msgConfirma = "Notificação já gerada hoje!";
//            return null;
//        }

        Dao dao = new Dao();
        dao.openTransaction();
        if (!dao.save(lote)) {
            GenericaMensagem.warn("Erro", "Erro ao Gerar Lote");
            dao.rollback();
            return null;
        }

        dao.commit();

        dao.openTransaction();
        if (!dao.executeQuery("INSERT INTO fin_cobranca (id_movimento,id_lote) (select m.id, " + lote.getId() + query + " GROUP BY m.id)")) {
            dao.rollback();
            dao.openTransaction();
            dao.delete(lote.getId());
            dao.commit();
            lote = new CobrancaLote();
            return null;
        } else {
            dao.commit();
        }

        dao.openTransaction();
        GenericaMensagem.info("Sucesso", "Gerado com sucesso!");
        itensLista.clear();

        listaNotificacao.clear();
        listaEmpresaAdd.clear();
        listaContabilAdd.clear();
        lote = new CobrancaLote();
        chkTodos = true;
        loadListaArquivos();
        return null;
    }

    public String gerarEtiquetas() {
        Dao dao = new Dao();
        CobrancaTipo ct = (CobrancaTipo) dao.find(new CobrancaTipo(), Integer.valueOf(listaTipoEnvio.get(idTipoEnvio).getDescription()));
        JuridicaDao dbJur = new JuridicaDao();
        PessoaEnderecoDao dbPesEnd = new PessoaEnderecoDao();
        NotificacaoDao db = new NotificacaoDao();

        List<Vector> result = db.listaParaEtiqueta(query, ct);

        List<ParametroEtiqueta> listax = new ArrayList();

        Juridica juridica;
        PessoaEndereco endereco;
        for (int i = 0; i < result.size(); i++) {
            try {
                // 6 - ETIQUETA PARA EMPRESAS
                // SE EMPRESA RETORNO DA QUERY id_pessoa (pes_pessoa)
                if (ct.getId() == 6) {
                    juridica = dbJur.pesquisaJuridicaPorPessoa((Integer) result.get(i).get(0));
                    endereco = dbPesEnd.pesquisaEndPorPessoaTipo(juridica.getPessoa().getId(), 2);
                } else {
                    // 7 - ETIQUETA PARA ESCRITÓRIOS
                    // SE ESCRITÓRIO RETORNO DA QUERY id_contabilidade (pes_juridica)
                    juridica = (Juridica) dao.find(new Juridica(), (Integer) result.get(i).get(0));
                    endereco = dbPesEnd.pesquisaEndPorPessoaTipo(juridica.getPessoa().getId(), 3);
                }

                listax.add(new ParametroEtiqueta(
                        juridica.getId(),
                        (juridica.getPessoa().getNome() != null) ? juridica.getPessoa().getNome() : "",
                        (endereco != null) ? endereco.getEndereco().getDescricaoEndereco().getDescricao() : "",
                        (endereco != null) ? endereco.getEndereco().getLogradouro().getDescricao() : "",
                        (endereco != null) ? endereco.getNumero() : "",
                        (endereco != null) ? endereco.getComplemento() : "",
                        (endereco != null) ? endereco.getEndereco().getBairro().getDescricao() : "",
                        (endereco != null) ? endereco.getEndereco().getCep() : "",
                        (endereco != null) ? endereco.getEndereco().getCidade().getCidade() : "",
                        (endereco != null) ? endereco.getEndereco().getCidade().getUf() : "",
                        (juridica.getPessoa().getTelefone1() != null) ? juridica.getPessoa().getTelefone1() : "",
                        (juridica.getPessoa().getEmail1() != null) ? juridica.getPessoa().getEmail1() : "",
                        (juridica.getPessoa().getTipoDocumento().getDescricao() != null) ? juridica.getPessoa().getTipoDocumento().getDescricao() : "",
                        (juridica.getPessoa().getDocumento() != null) ? juridica.getPessoa().getDocumento() : ""
                ));
            } catch (Exception e) {

            }
        }

        try {
            JRBeanCollectionDataSource dtSource = new JRBeanCollectionDataSource(listax);
            String string_jasper = "";
            if (ct.getId() == 6) {
                string_jasper = "/Relatorios/ETCONTRIBUINTES6181.jasper";
            } else {
                // USANDO O MESMO RELATÓRIO PARA OS DOIS TROCAR DEPOIS AQUI
                string_jasper = "/Relatorios/ETCONTRIBUINTES6181.jasper";
                //string_jasper = "/Relatorios/ETESCRITORIO6181.jasper";
            }

            File fl = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath(string_jasper));
            JasperReport jasperx = (JasperReport) JRLoader.loadObject(fl);
            JasperPrint print = JasperFillManager.fillReport(jasperx, null, dtSource);
            byte[] arquivo = new byte[0];

            arquivo = JasperExportManager.exportReportToPdf(print);

            String nomeDownload = "etiquetas_notificacao_" + DataHoje.horaMinuto().replace(":", "") + ".pdf";

            SalvaArquivos sa = new SalvaArquivos(arquivo, nomeDownload, false);
            String pathPasta = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/downloads/etiquetas");

            sa.salvaNaPasta(pathPasta);

            Download download = new Download(nomeDownload,
                    pathPasta,
                    "application/pdf",
                    FacesContext.getCurrentInstance());
            download.baixar();
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    public synchronized void enviarPeloMenu() {
        NotificacaoDao db = new NotificacaoDao();
        Dao dao = new Dao();
        boolean erro = false;
        Usuario usu = (Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("sessaoUsuario");
        if (usu.getId() == -1) {
            return;
        }
        Registro reg = (Registro) dao.find(new Registro(), 1);

        List<Vector> lista = db.pollingEmail(reg.getLimiteEnvios(), usu.getId());
        if (!lista.isEmpty()) {
            dao.openTransaction();

            for (int i = 0; i < lista.size(); i++) {
                PollingEmail pe = (PollingEmail) dao.find(new PollingEmail(), (Integer) lista.get(i).get(0));

                enviarEmailPolling(pe.getLinks());

                pe.setAtivo(false);
                pe.setEnvio(DataHoje.data());
                if (!dao.update(pe)) {
                    erro = true;
                    break;
                }
            }
        }

        if (!erro) {
            dao.commit();
            habilitaNot = false;
        } else {
            dao.rollback();
            habilitaNot = true;
            return;
        }

        dao.openTransaction();
        lista.clear();
        lista = db.pollingEmail(reg.getLimiteEnvios(), usu.getId());
        if (lista.isEmpty()) {
            lista = db.pollingEmailNovo(reg.getLimiteEnvios());
            if (!lista.isEmpty()) {
                String ph = DataHoje.incrementarHora(DataHoje.horaMinuto(), reg.getIntervaloEnvios());
                for (int i = 0; i < lista.size(); i++) {
                    PollingEmail pe = (PollingEmail) dao.find(new PollingEmail(), (Integer) lista.get(i).get(0));
                    pe.setAtivo(true);
                    pe.setEmissao(DataHoje.data());
                    pe.setHora(ph);

                    if (!dao.update(pe)) {
                        erro = true;
                        break;
                    }
                }
            }

        }

        if (!erro) {
            dao.commit();
            habilitaNot = false;
        } else {
            dao.rollback();
            habilitaNot = true;
        }
    }

    public void enviarNotificacao() throws JRException {
        if (lote.getId() == -1) {
            GenericaMensagem.warn("Erro", "Selecione um Lote para envio!");
            return;
        }

        if (((Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("sessaoUsuario")).getId() == -1) {
            return;
        }
        Juridica sindicato = (Juridica) new Dao().find(new Juridica(), 1);
        if (ca == null) {
            ca = (ConfiguracaoArrecadacao) new Dao().find(new ConfiguracaoArrecadacao(), 1);
        }
        String documentox = (ca.getFilial().getFilial().getPessoa().getDocumento().isEmpty() || ca.getFilial().getFilial().getPessoa().getDocumento().equals("0")) ? sindicato.getPessoa().getDocumento() : ca.getFilial().getFilial().getPessoa().getDocumento();

        HashMap params = new LinkedHashMap();

        params.put("sindicato_logo", ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoCliente.png"));
        params.put("sindicato_nome", ca.getFilial().getFilial().getPessoa().getNome());
        params.put("sindicato_documento", documentox);
        params.put("sindicato_site", ca.getFilial().getFilial().getPessoa().getSite());
        params.put("sindicato_logradouro", ca.getFilial().getFilial().getPessoa().getPessoaEndereco().getEndereco().getLogradouro().getDescricao());
        params.put("sindicato_endereco", ca.getFilial().getFilial().getPessoa().getPessoaEndereco().getEndereco().getDescricaoEndereco().getDescricao());
        params.put("sindicato_numero", ca.getFilial().getFilial().getPessoa().getPessoaEndereco().getNumero());
        params.put("sindicato_complemento", ca.getFilial().getFilial().getPessoa().getPessoaEndereco().getComplemento());
        params.put("sindicato_bairro", ca.getFilial().getFilial().getPessoa().getPessoaEndereco().getEndereco().getBairro().getDescricao());
        params.put("sindicato_cidade", ca.getFilial().getFilial().getPessoa().getPessoaEndereco().getEndereco().getCidade().getCidade());
        params.put("sindicato_uf", ca.getFilial().getFilial().getPessoa().getPessoaEndereco().getEndereco().getCidade().getUf());
        params.put("sindicato_cep", ca.getFilial().getFilial().getPessoa().getPessoaEndereco().getEndereco().getCep());
        params.put("sindicato_telefone", ca.getFilial().getFilial().getPessoa().getTelefone1());
        params.put("sindicato_email", ca.getFilial().getFilial().getPessoa().getEmail1());

        Dao dao = new Dao();
        CobrancaTipo ct = (CobrancaTipo) dao.find(new CobrancaTipo(), Integer.valueOf(listaTipoEnvio.get(idTipoEnvio).getDescription()));

        NotificacaoDao db = new NotificacaoDao();
        List<Vector> result = db.listaNotificacaoEnvio(ct.getId(), lote.getId());

        if (result.isEmpty()) {
            GenericaMensagem.warn("Erro", "Lista de parametros para envio vazia!");
            return;
        }

        List<ParametroNotificacao> listax = new ArrayList();
        CobrancaEnvio ce = db.pesquisaCobrancaEnvio(lote.getId());

        dao.openTransaction();
        if (ce.getId() == -1) {
            ce.setDtEmissao(DataHoje.dataHoje());
            ce.setHora(DataHoje.horaMinuto());
            ce.setLote(lote);
            ce.setUsuario(((Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("sessaoUsuario")));
            ce.setTipoCobranca(ct);
            if (!dao.save(ce)) {
                GenericaMensagem.warn("Erro", "Erro ao salvar Cobrança Envio");
                dao.rollback();
                return;
            }
        }

        // 4;"EMAIL PARA OS ESCRITÓRIO" 5;"EMAIL PARA AS EMPRESAS"
        if (ct.getId() == 4 || ct.getId() == 5) {
            List<Vector> lista = db.pollingEmailTrue();
            if (!lista.isEmpty()) {
                GenericaMensagem.warn("Erro", "Existem notificações às " + lista.get(0).get(1) + " para serem enviadas, conclua o envio antes de notificar mais!");
                dao.rollback();
                return;
            }

            int id_compara = 0;
            boolean enviar = false;

            Pessoa pes = new Pessoa();
            String jasper = "";

            int atual = 1;
            String ph = "";

            File fl_marca_d = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/notificacao_marca_dagua.png"));
            String marca_dagua = "";

            if (fl_marca_d.exists()) {
                marca_dagua = fl_marca_d.getPath();
            }

            for (int i = 0; i < result.size(); i++) {
                listax.add(
                        new ParametroNotificacao(
                                getConverteNullString(result.get(i).get(0)),
                                getConverteNullString(result.get(i).get(1)),
                                getConverteNullString(result.get(i).get(2)),
                                getConverteNullString(result.get(i).get(3)),
                                getConverteNullString(result.get(i).get(4)),
                                getConverteNullString(result.get(i).get(5)),
                                getConverteNullString(result.get(i).get(6)),
                                getConverteNullString(result.get(i).get(7)),
                                getConverteNullString(result.get(i).get(8)),
                                getConverteNullString(result.get(i).get(9)),
                                getConverteNullString(result.get(i).get(10)),
                                getConverteNullString(result.get(i).get(11)),
                                getConverteNullString(result.get(i).get(12)),
                                getConverteNullString(result.get(i).get(13)),
                                getConverteNullString(result.get(i).get(14)),
                                getConverteNullString(result.get(i).get(15)),
                                getConverteNullString(result.get(i).get(16)),
                                getConverteNullString(result.get(i).get(17)),
                                getConverteNullString(result.get(i).get(18)),
                                getConverteNullString(result.get(i).get(19)),
                                getConverteNullString(result.get(i).get(20)),
                                getConverteNullString(result.get(i).get(21)),
                                getConverteNullString(result.get(i).get(22)),
                                getConverteNullString(result.get(i).get(23)),
                                getConverteNullString(result.get(i).get(24)),
                                getConverteNullString(result.get(i).get(25)),
                                marca_dagua
                        )
                );

                try {
                    if (ct.getId() == 4) {
                        jasper = "NOTIFICACAO_ARRECADACAO_ESCRITORIO.jasper";
                        id_compara = getConverteNullInt(result.get(i).get(26)); // ID_JURIDICA
                        if (id_compara != getConverteNullInt(result.get(i + 1).get(26))) {
                            enviar = true;
                            pes = ((Juridica) dao.find(new Juridica(), id_compara)).getPessoa();
                        }
                    } else {
                        jasper = "NOTIFICACAO_ARRECADACAO_EMPRESA.jasper";
                        id_compara = getConverteNullInt(result.get(i).get(27)); // ID_PESSOA
                        if (id_compara != getConverteNullInt(result.get(i + 1).get(27))) {
                            enviar = true;
                            pes = (Pessoa) dao.find(new Pessoa(), id_compara);
                        }
                    }
                } catch (Exception e) {
                    if (ct.getId() == 4) {
                        pes = ((Juridica) dao.find(new Juridica(), id_compara)).getPessoa();
                    } else {
                        pes = (Pessoa) dao.find(new Pessoa(), id_compara);
                    }
                    enviar = true;
                }

                if (enviar) {
                    try {
                        if (atual <= registro.getLimiteEnvios() && ph.isEmpty()) {
                            if (!pes.getEmail1().isEmpty()) {
                                enviarEmail(pes, listax, dao, jasper, params);
                                atual++;
                            }
                        } else {
                            if (atual > registro.getLimiteEnvios() && ph.isEmpty()) {
                                atual = 1;
                                ph = DataHoje.incrementarHora(DataHoje.horaMinuto(), registro.getIntervaloEnvios());
                            }

                            JRBeanCollectionDataSource dtSource = new JRBeanCollectionDataSource(listax);

                            File fl = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Relatorios/" + jasper));
                            JasperReport jasperx = (JasperReport) JRLoader.loadObject(fl);
                            String nomeArq = "notificacao_";

                            JasperPrint print = JasperFillManager.fillReport(jasperx, params, dtSource);
                            byte[] arquivo = new byte[0];
                            arquivo = JasperExportManager.exportReportToPdf(print);

                            String nomeDownload = nomeArq + DataHoje.hora().replace(":", "") + ".pdf";
                            Thread.sleep(2000);
                            SalvaArquivos sa = new SalvaArquivos(arquivo, nomeDownload, false);
                            String pathPasta = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/notificacao/" + lote.getId());

                            File create = new File(pathPasta);
                            if (!create.exists()) {
                                create.mkdir();
                            }

                            sa.salvaNaPasta(pathPasta);

                            Links link = new Links();
                            link.setCaminho(registro.getUrlPath() + "/Sindical/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/notificacao/" + lote.getId());
                            link.setNomeArquivo(nomeDownload);
                            link.setPessoa(pes);
                            link.setDescricao(listaTipoEnvio.get(idTipoEnvio).getLabel());

                            if (!dao.save(link)) {
                                GenericaMensagem.warn("Erro", "Erro ao salvar Link de envio!");
                                dao.rollback();
                                return;
                            }

                            PollingEmail pe = new PollingEmail();
                            pe.setDtEmissao(DataHoje.dataHoje());
                            pe.setLinks(link);
                            pe.setCobrancaEnvio(ce);

                            if (atual <= registro.getLimiteEnvios()) {
                                pe.setAtivo(true);
                                pe.setHora(ph);
                            } else {
                                pe.setAtivo(false);
                            }

                            if (!dao.save(pe)) {
                                GenericaMensagem.warn("Erro", "Erro ao salvar Polling de envio!");
                                dao.rollback();
                                return;
                            }
                            atual++;
                        }
                    } catch (JRException | InterruptedException e) {
                        System.err.println(e.getMessage());
                    }
                    enviar = false;
                    listax.clear();

                }

            }

        } else {

            boolean imprimir = false, adicionar_jasper = false;

            int atual = 0, limite = 3000;

            List<JasperPrint> listJasper = new ArrayList();

            String load_file = "";

            switch (ct.getId()) {
                // 1;"ESCRITÓRIO"
                case 1:
                    load_file = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Relatorios/NOTIFICACAO_ARRECADACAO_ESCRITORIO.jasper");
                    break;
                // 2;"EMPRESA COM ESCRITÓRIO"
                // 3;"EMPRESA SEM ESCRITÓRIO"
                case 2:
                case 3:
                    load_file = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Relatorios/NOTIFICACAO_ARRECADACAO_EMPRESA.jasper");
                    break;
                case 8:
                    load_file = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Relatorios/NOTIFICACAO_ARRECADACAO_ESCRITORIO_RESUMO.jasper");
                    break;
                default:
                    break;
            }

            JasperReport jasper = (JasperReport) JRLoader.loadObject(new File(load_file));

            File fl_marca_d = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/notificacao_marca_dagua.png"));
            String marca_dagua = null;

            if (fl_marca_d.exists()) {
                marca_dagua = fl_marca_d.getPath();
            }
            for (int i = 0; i < result.size(); i++) {
                listax.add(
                        new ParametroNotificacao(
                                getConverteNullString(result.get(i).get(0)),
                                getConverteNullString(result.get(i).get(1)),
                                getConverteNullString(result.get(i).get(2)),
                                getConverteNullString(result.get(i).get(3)),
                                getConverteNullString(result.get(i).get(4)),
                                getConverteNullString(result.get(i).get(5)),
                                getConverteNullString(result.get(i).get(6)),
                                getConverteNullString(result.get(i).get(7)),
                                getConverteNullString(result.get(i).get(8)),
                                getConverteNullString(result.get(i).get(9)),
                                getConverteNullString(result.get(i).get(10)),
                                getConverteNullString(result.get(i).get(11)),
                                getConverteNullString(result.get(i).get(12)),
                                getConverteNullString(result.get(i).get(13)),
                                getConverteNullString(result.get(i).get(14)),
                                getConverteNullString(result.get(i).get(15)),
                                getConverteNullString(result.get(i).get(16)),
                                getConverteNullString(result.get(i).get(17)),
                                getConverteNullString(result.get(i).get(18)),
                                getConverteNullString(result.get(i).get(19)),
                                getConverteNullString(result.get(i).get(20)),
                                getConverteNullString(result.get(i).get(21)),
                                getConverteNullString(result.get(i).get(22)),
                                getConverteNullString(result.get(i).get(23)),
                                getConverteNullString(result.get(i).get(24)),
                                getConverteNullString(result.get(i).get(25)),
                                marca_dagua
                        )
                );

                try {
                    int id_compara;
                    switch (ct.getId()) {
                        // 1;"ESCRITÓRIO"
                        case 1:
                        case 8:
                            id_compara = getConverteNullInt(result.get(i).get(26)); // ID_JURIDICA
                            if (id_compara != getConverteNullInt(result.get(i + 1).get(26)) && !adicionar_jasper) {
                                adicionar_jasper = true;
                            }
                            break;
                        // 2;"EMPRESA COM ESCRITÓRIO"
                        // 3;"EMPRESA SEM ESCRITÓRIO"
                        case 2:
                        case 3:
                            id_compara = getConverteNullInt(result.get(i).get(27)); // ID_PESSOA
                            if (id_compara != getConverteNullInt(result.get(i + 1).get(27)) && !adicionar_jasper) {
                                adicionar_jasper = true;
                            }
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                    adicionar_jasper = true;
                    imprimir = true;
                    atual = limite;
                }

                try {

                    if (adicionar_jasper) {

                        JRBeanCollectionDataSource dtSource = new JRBeanCollectionDataSource(listax);

                        //* JASPER FRENTE *//
                        JasperPrint print = JasperFillManager.fillReport(jasper, params, dtSource);
                        listJasper.add(print);

                        // 3;"EMPRESA SEM ESCRITÓRIO"
                        if (ct.getId() == 3 && chkImprimirVerso) {
                            //* JASPER VERSO *//
                            JasperReport jasper_verso = (JasperReport) JRLoader.loadObject(
                                    new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Relatorios/NOTIFICACAO_VERSO.jasper"))
                            );

                            HashMap params_verso = new LinkedHashMap();

                            Pessoa p_resp = (Pessoa) dao.find(new Pessoa(), getConverteNullInt(result.get(i).get(27)));

                            params_verso.put("serrilha", ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Imagens/serrilha.GIF")); // SERRILHA
                            params_verso.put("logo_verso", ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoCliente.png"));
                            params_verso.put("sindicato_nome", ca.getFilial().getFilial().getPessoa().getNome());
                            params_verso.put("sindicato_endereco", ca.getFilial().getFilial().getPessoa().getPessoaEndereco().getEnderecoCompletoSemComplementoString());
                            params_verso.put("sindicato_complemento", ca.getFilial().getFilial().getPessoa().getPessoaEndereco().getComplemento());
                            params_verso.put("responsavel_nome", p_resp.getNome());
                            params_verso.put("responsavel_endereco", p_resp.getPessoaEndereco().getEnderecoCompletoSemComplementoString());
                            params_verso.put("responsavel_complemento", p_resp.getPessoaEndereco().getComplemento());
                            params_verso.put("servico_nome", listax.get(0).getMovservico());

                            print = JasperFillManager.fillReport(jasper_verso, params_verso);
                            listJasper.add(print);
                        }

                        adicionar_jasper = false;
                        listax = new ArrayList();
                    }

                    if (imprimir || (atual >= limite)) {
                        String nomeArq = "notificacao_";

                        String pathPasta = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/notificacao/" + lote.getId());

                        File create = new File(pathPasta);
                        if (!create.exists()) {
                            create.mkdirs();
                        }

                        Thread.sleep(2000);

                        String nomeDownload = nomeArq + DataHoje.hora().replace(":", "") + DataHoje.data().replace("/", "") + ".pdf";

                        File file = new File(pathPasta + "/" + nomeDownload);

                        JRPdfExporter exporter = new JRPdfExporter();
                        exporter.setExporterInput(SimpleExporterInput.getInstance(listJasper));
                        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(file.getPath()));
                        SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
                        configuration.setCreatingBatchModeBookmarks(true);
                        exporter.setConfiguration(configuration);
                        exporter.exportReport();

                        Links link = new Links();
                        link.setCaminho(registro.getUrlPath() + "/Sindical/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/notificacao/" + lote.getId());
                        link.setNomeArquivo(nomeDownload);
                        link.setPessoa(null);
                        link.setDescricao(listaTipoEnvio.get(idTipoEnvio).getLabel());

                        if (!dao.save(link)) {
                            GenericaMensagem.warn("Erro", "Erro ao salvar Link de envio!");
                            dao.rollback();
                            return;
                        }

                        imprimir = false;
                        atual = 0;
                        listax.clear();
                    }

                } catch (JRException | InterruptedException e) {
                    System.err.println(e.getMessage());
                }
                atual++;
            }
        }

        if (!result.isEmpty()) {
            dao.commit();
        }

        //listaNotificacao.clear();
        loadListaArquivos();
    }

    public String enviarEmailPolling(Links link) {
        try {
            if (link.getPessoa().getEmail1().isEmpty()) {
                return null;
            }
            List<Pessoa> pes_add = new ArrayList();
            pes_add.add(link.getPessoa());
            String pathPasta = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/notificacao/" + lote.getId());

            List<File> fls = new ArrayList();
            String mensagem;

            if (!registro.isEnviarEmailAnexo()) {
                mensagem = " <h5> Visualize sua notificação clicando no link abaixo </5> <br /><br />"
                        + " <a href='" + registro.getUrlPath() + "/Sindical/acessoLinks.jsf?cliente=" + ControleUsuarioBean.getCliente() + "&amp;arquivo=" + link.getNomeArquivo() + "' target='_blank'>Clique aqui para abrir a notificacão</a><br />";
            } else {
                fls.add(new File(pathPasta + "/" + link.getNomeArquivo()));
                mensagem = "<h5>Baixe sua notificação anexado neste email</5><br /><br />";
            }

            DaoInterface di = new Dao();
            Mail mail = new Mail();
            mail.setFiles(fls);
            mail.setEmail(
                    new Email(
                            -1,
                            DataHoje.dataHoje(),
                            DataHoje.livre(new Date(), "HH:mm"),
                            (Usuario) GenericaSessao.getObject("sessaoUsuario"),
                            (Rotina) di.find(new Rotina(), 106),
                            null,
                            "Notificação",
                            mensagem,
                            false,
                            false
                    )
            );
            List<EmailPessoa> emailPessoas = new ArrayList();
            EmailPessoa emailPessoa = new EmailPessoa();

            for (Pessoa pe : pes_add) {
                emailPessoa.setDestinatario(pe.getEmail1());
                emailPessoa.setPessoa(pe);
                emailPessoa.setRecebimento(null);
                emailPessoas.add(emailPessoa);
                mail.setEmailPessoas(emailPessoas);
                emailPessoa = new EmailPessoa();
            }

            String[] retorno = mail.send("personalizado");
            if (!retorno[1].isEmpty()) {
                GenericaMensagem.warn("Erro", retorno[1]);
            } else {
                GenericaMensagem.info("Sucesso", retorno[0]);
            }
        } catch (Exception e) {
        }
        return null;
    }

    public String enviarEmail(Pessoa pessoa, List<ParametroNotificacao> lista, Dao dao, String nomeJasper, HashMap params) {
        JRBeanCollectionDataSource dtSource = new JRBeanCollectionDataSource(lista);
        String nomeArq = "notificacao_";
        try {
            File fl = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Relatorios/" + nomeJasper));
            JasperReport jasper = (JasperReport) JRLoader.loadObject(fl);
            JasperPrint print = JasperFillManager.fillReport(jasper, params, dtSource);

            byte[] arquivo = JasperExportManager.exportReportToPdf(print);

            String nomeDownload = nomeArq + DataHoje.hora().replace(":", "") + ".pdf";
            SalvaArquivos sa = new SalvaArquivos(arquivo, nomeDownload, false);
            String pathPasta = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/notificacao/" + lote.getId());

            File create = new File(pathPasta);
            if (!create.exists()) {
                create.mkdir();
            }

            sa.salvaNaPasta(pathPasta);

            Links link = new Links();
            link.setCaminho(registro.getUrlPath() + "/Sindical/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/notificacao/" + lote.getId());
            link.setNomeArquivo(nomeDownload);
            link.setPessoa(pessoa);
            link.setDescricao(listaTipoEnvio.get(idTipoEnvio).getLabel());

            if (!dao.save(link)) {
                GenericaMensagem.warn("Erro", "Erro ao salvar Link de envio!");
                dao.rollback();
                return null;
            }

            List<Pessoa> pes_add = new ArrayList();
            pes_add.add(pessoa);

            List<File> fls = new ArrayList();
            String mensagem;

            if (!registro.isEnviarEmailAnexo()) {
                mensagem = " <h5> Visualize sua notificação clicando no link abaixo </5> <br /><br />"
                        + " <a href='" + registro.getUrlPath() + "/Sindical/acessoLinks.jsf?cliente=" + ControleUsuarioBean.getCliente() + "&amp;arquivo=" + nomeDownload + "' target='_blank'>Clique aqui para abrir a notificacão</a><br />";
            } else {
                fls.add(new File(pathPasta + "/" + link.getNomeArquivo()));
                mensagem = "<h5>Baixe sua notificação anexado neste email</5><br /><br />";
            }

            DaoInterface di = new Dao();
            Mail mail = new Mail();
            mail.setFiles(fls);
            mail.setEmail(
                    new Email(
                            -1,
                            DataHoje.dataHoje(),
                            DataHoje.livre(new Date(), "HH:mm"),
                            (Usuario) GenericaSessao.getObject("sessaoUsuario"),
                            (Rotina) di.find(new Rotina(), 106),
                            null,
                            "Notificação",
                            mensagem,
                            false,
                            false
                    )
            );
            List<EmailPessoa> emailPessoas = new ArrayList();
            EmailPessoa emailPessoa = new EmailPessoa();

            for (Pessoa pe : pes_add) {
                emailPessoa.setDestinatario(pe.getEmail1());
                emailPessoa.setPessoa(pe);
                emailPessoa.setRecebimento(null);
                emailPessoas.add(emailPessoa);
                mail.setEmailPessoas(emailPessoas);
                emailPessoa = new EmailPessoa();
            }

            String[] retorno = mail.send("personalizado");

            if (!retorno[1].isEmpty()) {
                GenericaMensagem.warn("Erro", retorno[1]);
            } else {
                GenericaMensagem.info("Sucesso", retorno[0]);
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    public void alteraCombo() {
        if (!itensLista.isEmpty()) {
            if (Integer.valueOf(itensLista.get(idLista).getDescription()) == -1) {
                lote = new CobrancaLote();
            } else {
                lote = (CobrancaLote) new Dao().find(new CobrancaLote(), Integer.valueOf(itensLista.get(idLista).getDescription()));
            }
        }
        listaNotificacao.clear();
        loadListaArquivos();
    }

    public String getConverteNullString(Object object) {
        if (object == null) {
            return "";
        } else {
            return String.valueOf(object);
        }
    }

    public int getConverteNullInt(Object object) {
        if (object == null) {
            return 0;
        } else {
            return (Integer) object;
        }
    }

    public int getIdLista() {
        return idLista;
    }

    public void setIdLista(int idLista) {
        this.idLista = idLista;
    }

    public List<SelectItem> getItensLista() {
        if (itensLista.isEmpty()) {
            NotificacaoDao db = new NotificacaoDao();
            List<CobrancaLote> result = db.listaCobrancaLote();
            itensLista.add(new SelectItem(0, "<< Gerar novo Lote de Notificação >>", String.valueOf(-1)));
            for (int i = 0; i < result.size(); i++) {
                itensLista.add(new SelectItem(i + 1,
                        "Lote gerado - " + result.get(i).getEmissao() + " às " + result.get(i).getHora() + " - " + result.get(i).getUsuario().getLogin(),
                        String.valueOf(result.get(i).getId())));
            }
        }
        return itensLista;
    }

    public void setItensLista(List<SelectItem> itensLista) {
        this.itensLista = itensLista;
    }

    public CobrancaLote getLote() {
        return lote;
    }

    public void setLote(CobrancaLote lote) {
        this.lote = lote;
    }

    public List<SelectItem> getListaTipoEnvio() {
        if (listaTipoEnvio.isEmpty()) {
            NotificacaoDao db = new NotificacaoDao();
            List<CobrancaTipo> result = db.listaCobrancaTipoEnvio();
            for (int i = 0; i < result.size(); i++) {
                listaTipoEnvio.add(new SelectItem(new Integer(i),
                        result.get(i).getDescricao(),
                        String.valueOf(result.get(i).getId())));
            }
        }
        return listaTipoEnvio;
    }

    public void setListaTipoEnvio(List<SelectItem> listaTipoEnvio) {
        this.listaTipoEnvio = listaTipoEnvio;
    }

    public int getIdTipoEnvio() {
        return idTipoEnvio;
    }

    public void setIdTipoEnvio(int idTipoEnvio) {
        this.idTipoEnvio = idTipoEnvio;
    }

    public int getQuantidade() {
//        if (quantidade == 0){
//            getListaNotificacao();
//            quantidade = listaNotificacao.size();
//        }
        quantidade = listaNotificacao.size();
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public void marcarTodos() {
        for (int i = 0; i < listaNotificacao.size(); i++) {
            listaNotificacao.get(i).setArgumento0(chkTodos);
        }
    }

    public boolean isChkTodos() {
        return chkTodos;
    }

    public void setChkTodos(boolean chkTodos) {
        this.chkTodos = chkTodos;
    }

    public List<DataObject> getListaEmpresaAdd() {
        if (GenericaSessao.exists("juridicaPesquisa") && empresa) {
            Juridica j = (Juridica) GenericaSessao.getObject("juridicaPesquisa");

            for (int i = 0; i < listaEmpresaAdd.size(); i++) {
                if (listaEmpresaAdd.get(i).getArgumento0().equals(j.getId())) {
                    GenericaSessao.remove("juridicaPesquisa");
                    return listaEmpresaAdd;
                }
            }

            listaEmpresaAdd.add(new DataObject(j.getId(), j));
            GenericaSessao.remove("juridicaPesquisa");
            listaNotificacao.clear();
        }
        return listaEmpresaAdd;
    }

    public void setListaEmpresaAdd(List<DataObject> listaEmpresaAdd) {
        this.listaEmpresaAdd = listaEmpresaAdd;
    }

    public List<DataObject> getListaContabilAdd() {
        if (GenericaSessao.exists("juridicaPesquisa") && !empresa) {
            Juridica j = (Juridica) GenericaSessao.getObject("juridicaPesquisa");

            for (int i = 0; i < listaContabilAdd.size(); i++) {
                if (listaContabilAdd.get(i).getArgumento0().equals(j.getId())) {
                    GenericaSessao.remove("juridicaPesquisa");
                    return listaContabilAdd;
                }
            }

            listaContabilAdd.add(new DataObject(j.getId(), j));
            GenericaSessao.remove("juridicaPesquisa");
            listaNotificacao.clear();
        }
        return listaContabilAdd;
    }

    public void setListaContabilAdd(List<DataObject> listaContabilAdd) {
        this.listaContabilAdd = listaContabilAdd;
    }

    public boolean isHabilitaNot() {
        NotificacaoDao db = new NotificacaoDao();
        Registro reg = (Registro) new Dao().find(new Registro(), 1);

        Usuario usu = (Usuario) GenericaSessao.getObject("sessaoUsuario");
        if (usu == null || (usu != null && usu.getId() == -1)) {
            return false;
        }
        if (reg != null) {
            List lista = db.pollingEmail(reg.getLimiteEnvios(), ((Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("sessaoUsuario")).getId());
            if (!lista.isEmpty()) {
                habilitaNot = true;
                quantidadeMenu = lista.size();
            } else {
                habilitaNot = false;
            }
        }
        return habilitaNot;
    }

    public void setHabilitaNot(boolean habilitaNot) {
        this.habilitaNot = habilitaNot;
    }

    public int getQuantidadeMenu() {
        return quantidadeMenu;
    }

    public void setQuantidadeMenu(int quantidadeMenu) {
        this.quantidadeMenu = quantidadeMenu;
    }

    public List<DataObject> getListaCidadesBase() {
        if (listaCidadesBase.isEmpty()) {
            GrupoCidadesDao db = new GrupoCidadesDao();
            List select = new ArrayList();
            select.addAll(db.pesquisaCidadesBase());
            for (int i = 0; i < select.size(); i++) {
                listaCidadesBase.add(new DataObject(false, ((Cidade) select.get(i))));
            }
        }
        return listaCidadesBase;
    }

    public boolean isChkCidadesBase() {
        return chkCidadesBase;
    }

    public void setChkCidadesBase(boolean chkCidadesBase) {
        this.chkCidadesBase = chkCidadesBase;
    }

    public void marcarCidadesBase() {
        for (int i = 0; i < listaCidadesBase.size(); i++) {
            listaCidadesBase.get(i).setArgumento0(chkCidadesBase);
        }
        listaNotificacao.clear();
    }

    public void marcarServicos() {
        for (int i = 0; i < listaServicos.size(); i++) {
            listaServicos.get(i).setChk(chkServicos);
        }
        listaNotificacao.clear();
    }

    public void marcarTipoServico() {
        for (int i = 0; i < listaTipoServico.size(); i++) {
            listaTipoServico.get(i).setChk(chkTipoServico);
        }
        listaNotificacao.clear();
    }

    public String getTabAtiva() {
        return tabAtiva;
    }

    public void setTabAtiva(String tabAtiva) {
        this.tabAtiva = tabAtiva;
    }

    public int getValorAtual() {
        return valorAtual;
    }

    public void setValorAtual(int valorAtual) {
        this.valorAtual = valorAtual;
    }

    public boolean isProgressoAtivo() {
        return progressoAtivo;
    }

    public void setProgressoAtivo(boolean progressoAtivo) {
        this.progressoAtivo = progressoAtivo;
    }

    public List<DataObject> getListaArquivo() {
        return listaArquivo;
    }

    public void setListaArquivo(List<DataObject> listaArquivo) {
        this.listaArquivo = listaArquivo;
    }

    public int getIndexTab() {
        return indexTab;
    }

    public void setIndexTab(int indexTab) {
        this.indexTab = indexTab;
    }

    public Boolean getChkServicos() {
        return chkServicos;
    }

    public void setChkServicos(Boolean chkServicos) {
        this.chkServicos = chkServicos;
    }

    public List<ListaDeServicos> getListaServicos() {
        if (listaServicos.isEmpty()) {
            ServicoRotinaDao dbsr = new ServicoRotinaDao();
            List<Servicos> s = dbsr.listaServicosIn("1,2,3,4");

            for (Servicos item : s) {
                listaServicos.add(new ListaDeServicos(false, item));
            }
        }
        return listaServicos;
    }

    public void setListaServicos(List<ListaDeServicos> listaServicos) {
        this.listaServicos = listaServicos;
    }

    public Boolean getChkTipoServico() {
        return chkTipoServico;
    }

    public void setChkTipoServico(Boolean chkTipoServico) {
        this.chkTipoServico = chkTipoServico;
    }

    public List<ListaDeTipoServico> getListaTipoServico() {
        if (listaTipoServico.isEmpty()) {
            List<TipoServico> ts = new Dao().list(new TipoServico());

            for (TipoServico item : ts) {
                listaTipoServico.add(new ListaDeTipoServico(false, item));
            }
        }
        return listaTipoServico;
    }

    public void setListaTipoServico(List<ListaDeTipoServico> listaTipoServico) {
        this.listaTipoServico = listaTipoServico;
    }

    public Boolean getChkImprimirVerso() {
        return chkImprimirVerso;
    }

    public void setChkImprimirVerso(Boolean chkImprimirVerso) {
        this.chkImprimirVerso = chkImprimirVerso;
    }

    public class ListaDeServicos {

        private Boolean chk = false;
        private Servicos servicos = new Servicos();

        public ListaDeServicos(Boolean chk, Servicos servicos) {
            this.chk = chk;
            this.servicos = servicos;
        }

        public Boolean getChk() {
            return chk;
        }

        public void setChk(Boolean chk) {
            this.chk = chk;
        }

        public Servicos getServicos() {
            return servicos;
        }

        public void setServicos(Servicos servicos) {
            this.servicos = servicos;
        }
    }

    public class ListaDeTipoServico {

        private Boolean chk = false;
        private TipoServico tipoServico = new TipoServico();

        public ListaDeTipoServico(Boolean chk, TipoServico tipoServico) {
            this.chk = chk;
            this.tipoServico = tipoServico;
        }

        public Boolean getChk() {
            return chk;
        }

        public void setChk(Boolean chk) {
            this.chk = chk;
        }

        public TipoServico getTipoServico() {
            return tipoServico;
        }

        public void setTipoServicos(TipoServico tipoServico) {
            this.tipoServico = tipoServico;
        }
    }

}
