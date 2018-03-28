package br.com.rtools.impressao.beans;

import br.com.rtools.associativo.ConfiguracaoSocial;
import br.com.rtools.associativo.ConviteMovimento;
import br.com.rtools.impressao.ConviteClube;
import br.com.rtools.impressao.ParametroProtocolo;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.PessoaEndereco;
import br.com.rtools.pessoa.dao.PessoaEnderecoDao;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.sistema.Links;
import br.com.rtools.sistema.dao.LinksDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.Diretorio;
import br.com.rtools.utilitarios.Download;
import br.com.rtools.utilitarios.EnviarEmail;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaString;
import br.com.rtools.utilitarios.Jasper;
import br.com.rtools.utilitarios.SalvaArquivos;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

@ManagedBean
@ViewScoped
public class ImprimirConviteClube implements Serializable {

    private ConfiguracaoSocial cs = new ConfiguracaoSocial();

    public ImprimirConviteClube() {
        cs = (ConfiguracaoSocial) new Dao().find(new ConfiguracaoSocial(), 1);
    }

    public void imprimir(ConviteMovimento cm) {
        Collection lista = parametroConvite(cm);
//            File fl = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Relatorios/CONVITE_CLUBE.jasper"));
//            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(fl);
//            JRBeanCollectionDataSource dtSource = new JRBeanCollectionDataSource(lista);
//            JasperPrint print = JasperFillManager.fillReport(jasperReport, null, dtSource);
//            Diretorio.criar("Arquivos/downloads/convite");
//            byte[] arquivo = JasperExportManager.exportReportToPdf(print);
//            String nomeDownload = "imp_convite_clube_" + cm.getId() + ".pdf";
//            String pathPasta = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/downloads/convite");
//            SalvaArquivos salvaArquivos = new SalvaArquivos(arquivo, nomeDownload, false);
//            salvaArquivos.salvaNaPasta(pathPasta);
//            Download download = new Download(nomeDownload, pathPasta, "application/pdf", FacesContext.getCurrentInstance());
//            download.baixar();
//            download.remover();
        if (ConfiguracaoSocial.get().getConviteCartaoPvc()) {
            Jasper.printMedia("/Relatorios/CONVITE_CLUBE_CARTAO.jasper", "convite_clube", lista, "pdf");
        } else {
            Jasper.printMedia("/Relatorios/CONVITE_CLUBE.jasper", "convite_clube", lista, "pdf");
        }
//        try {
//        } catch (JRException e) {
//            e.getMessage();
//        }
    }

    public void enviar(ConviteMovimento cm) {
        if (cm.getSisPessoa().getEmail1().isEmpty()) {
            GenericaMensagem.info("Validação", "Informar e-mail");
            return;
        }
        try {
            Collection lista = parametroConvite(cm);
            File fl = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Relatorios/CONVITE_CLUBE.jasper"));
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(fl);
            JRBeanCollectionDataSource dtSource = new JRBeanCollectionDataSource(lista);
            JasperPrint print = JasperFillManager.fillReport(jasperReport, null, dtSource);
            byte[] arquivo = JasperExportManager.exportReportToPdf(print);
            String nomeDownload = "imp_convite_clube_" + cm.getId() + ".pdf";
            String pathPasta = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/downloads/protocolo");
            Diretorio.criar("Arquivos/downloads/convite");
            SalvaArquivos salvaArquivos = new SalvaArquivos(arquivo, nomeDownload, false);
            salvaArquivos.salvaNaPasta(pathPasta);
            LinksDao db = new LinksDao();
            Links link = db.pesquisaNomeArquivo(nomeDownload);
            Dao dao = new Dao();
            if (link == null) {
                link = new Links();
                link.setCaminho("/Sindical/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/downloads/convite");
                link.setNomeArquivo(nomeDownload);
                dao.openTransaction();
                if (dao.save(link)) {
                    dao.commit();
                } else {
                    dao.rollback();
                }
            }
            Pessoa pessoa = new Pessoa();
            pessoa.setNome(cm.getSisPessoa().getNome());
            pessoa.setDocumento(cm.getSisPessoa().getDocumento());
            pessoa.setEmail1(cm.getSisPessoa().getEmail1());
            List<Pessoa> p = new ArrayList();
            p.add(pessoa);
            String[] ret;
            Registro registro = Registro.get();
            if (registro.isEnviarEmailAnexo()) {
                List<File> fls = new ArrayList<File>();
                fls.add(new File(pathPasta + "/" + nomeDownload));
                ret = EnviarEmail.EnviarEmailPersonalizado(registro, p, " <h5>Baixe seu convite que esta anexado neste email</5><br /><br />", fls, "Envio de convite clube");
                salvaArquivos.remover();
            } else {
                ret = EnviarEmail.EnviarEmailPersonalizado(registro,
                        p,
                        " <h5>Visualize seu protocolo clicando no link abaixo</5><br /><br />"
                        + " <a href='" + registro.getUrlPath() + "/Sindical/acessoLinks.jsf?cliente=" + ControleUsuarioBean.getCliente() + "&amp;arquivo=" + nomeDownload + "' target='_blank'>Clique aqui para abrir seu protocolo</a><br />",
                        //" <a href='"+registro.getUrlPath()+"/Sindical/Arquivos/downloads/protocolo/"+nomeDownload+".pdf' target='_blank'>Clique aqui para abrir seu protocolo</a><br />", 
                        new ArrayList(),
                        "Envio de convite clube");

            }
            if (!ret[1].isEmpty()) {
                GenericaMensagem.info("E-mail", ret[1]);
            } else {
                GenericaMensagem.info("E-mail", ret[0]);
            }
        } catch (JRException e) {
            NovoLog log = new NovoLog();
            log.live("Erro de envio de protocolo por e-mail: Mensagem: " + e.getMessage());
        }
    }

    public PessoaEndereco pessoaEndereco(Filial f) {
        PessoaEndereco pessoaEndereco = new PessoaEndereco();
        if (f.getId() != -1) {
            PessoaEnderecoDao dao = new PessoaEnderecoDao();
            pessoaEndereco = dao.pesquisaEndPorPessoaTipo(f.getFilial().getPessoa().getId(), 2);
        }
        return pessoaEndereco;
    }

    public Collection<ConviteClube> parametroConvite(ConviteMovimento cm) {
        if (cm.getId() == -1) {
            return new ArrayList();
        }
        Collection lista = new ArrayList();
        DataHoje dh = new DataHoje();

        List listSemana = new ArrayList();
        if (cm.getConviteServico() != null) {
            if (cm.getConviteServico().isDomingo()) {
                listSemana.add("Dom");
            }
            if (cm.getConviteServico().isSegunda()) {
                listSemana.add("Seg");
            }
            if (cm.getConviteServico().isTerca()) {
                listSemana.add("Ter");
            }
            if (cm.getConviteServico().isQuarta()) {
                listSemana.add("Qua");
            }
            if (cm.getConviteServico().isQuinta()) {
                listSemana.add("Qui");
            }
            if (cm.getConviteServico().isSexta()) {
                listSemana.add("Sex");
            }
            if (cm.getConviteServico().isSabado()) {
                listSemana.add("Sáb");
            }
            if (cm.getConviteServico().isFeriado()) {
                listSemana.add("Feriado");
            }
        }

        String s_barras = "00000000".substring(0, 8 - ("" + cm.getId()).length()) + cm.getId();
        String barras = "";

        int cont_via = 0, cont_codigo = 0;
        for (int i = 0; i < cs.getCartaoDigitos(); i++) {
            if (cs.getCartaoPosicaoVia() == i) {
                barras += "99";
                i = i + 1;
                continue;
            }

            if (cs.getCartaoPosicaoCodigo() == i) {
                barras += s_barras;
                i = i + 7;
                continue;
            }
            barras += "0";
        }

        String img = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoConvite.png");
        if (ConfiguracaoSocial.get().getConviteCartaoPvc()) {
            img = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoCliente.png");
        }

        lista.add(new ConviteClube(
                cm.getSisPessoa().getNome(),
                cm.getDtEmissao(),
                "VÁLIDO ATÉ " + cm.getValidade(),
                img,
                barras,
                (!cm.isCortesia()) ? "NO(S) DIA(S): " + listSemana : "CORTESIA PARA OS DIAS: " + listSemana,
                cm.getSisPessoa().getObservacao(),
                cs.getObservacaoConvite()
        ));
        return lista;
    }
}
