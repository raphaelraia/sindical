package br.com.rtools.relatorios.beans;

import br.com.rtools.associativo.EventoBaile;
import br.com.rtools.associativo.dao.VendaBaileDao;
import br.com.rtools.impressao.ParametroFechamentoBaile;
import br.com.rtools.relatorios.dao.RelatorioFechamentoBaileDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.Jasper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class RelatorioFechamentoBaileBean implements Serializable {

    private final List<SelectItem> listaEventoBaile = new ArrayList();
    private Integer idEventoBaile = 0;
    private Boolean EXPORT_TO = false;
    private Boolean mostrar_todos = false;

    @PostConstruct
    public void init() {
        loadListaEventoBaile();
    }

    @PreDestroy
    public void destroy() {

    }

    public void clear(Integer tcase) {
        switch (tcase) {
            case 1:
                loadListaEventoBaile();
                break;
        }
    }

    // CLASS DE TESTES PARA exportPDF exportXLS exportexcel export excel
//    public void imprimirJasper() {
//        File file_jasper = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Relatorios/FECHAMENTO_BAILE.jasper"));
//        try {
//            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(file_jasper);
//
//            RelatorioFechamentoBaileDao dao = new RelatorioFechamentoBaileDao();
//
//            List<Vector> result = dao.listaEventoBaile(Integer.valueOf(listaEventoBaile.get(idEventoBaile).getDescription()));
//
//            // COM PARAMETRO FUNCIONA NORMAL
////            List<ParametroFechamentoBaile> lista = new ArrayList();
////            for (Vector v : result) {
////                lista.add(
////                        new ParametroFechamentoBaile(
////                                DataHoje.converteData((Date) v.get(0)), // EMISSAO
////                                v.get(1).toString(), // OPERADOR
////                                v.get(2).toString(), // CODIGO
////                                v.get(3).toString(), // CONVIDADO
////                                ((Integer) v.get(12) == 13 || (Integer) v.get(12) == 15) ? "Cortesia" : v.get(4).toString(), // STATUS
////                                (v.get(5) != null ? v.get(5).toString() : ""), // MESA
////                                (v.get(6) != null ? v.get(6).toString() : ""), // CONVITE
////                                DataHoje.converteData((Date) v.get(7)), // VENCIMENTO
////                                DataHoje.converteData((Date) v.get(8)), // PAGAMENTO
////                                (v.get(9) != null ? ((Double) v.get(9)).floatValue() : 0), // VALOR
////                                (v.get(10) != null ? v.get(10).toString() : ""), // CAIXA
////                                v.get(11).toString() // OBS
////                        )
////                );
////            }
//            HashMap<String, Object> hm = new LinkedHashMap();
//            List<HashMap> lista = new ArrayList();
//            for (Vector v : result) {
//                hm = new LinkedHashMap();
//                hm.put("emissao", DataHoje.converteData((Date) v.get(0)));
//                hm.put("operador", v.get(1).toString());
//                hm.put("codigo", v.get(2).toString());
//                hm.put("convidado", v.get(3).toString());
//                hm.put("status", ((Integer) v.get(12) == 13 || (Integer) v.get(12) == 15) ? "Cortesia" : v.get(4).toString());
//                hm.put("mesa", (v.get(5) != null ? v.get(5).toString() : ""));
//                hm.put("convite", (v.get(6) != null ? v.get(6).toString() : ""));
//                hm.put("vencimento", DataHoje.converteData((Date) v.get(7)));
//                hm.put("pagamento", DataHoje.converteData((Date) v.get(8)));
//                hm.put("valor", (v.get(9) != null ? ((Double) v.get(9)).floatValue() : 0));
//                hm.put("caixa", (v.get(10) != null ? v.get(10).toString() : ""));
//                hm.put("obs", v.get(11).toString());
//                lista.add(hm);
//            }
//
//            JRBeanCollectionDataSource dtSource = new JRBeanCollectionDataSource(lista);
//            HashMap param = new HashMap();
//            // MOEDA PARA BRASIL VALORES IREPORT PTBR CONVERTE VALOR JASPER VALOR IREPORT VALOR
//            param.put("REPORT_LOCALE", new Locale("pt", "BR"));
//            JasperPrint print = JasperFillManager.fillReport(jasperReport, param, dtSource);
//            List<JasperPrint> list = new ArrayList();
//
//            list.add(print);
//
//            Boolean pdf = false;
//            Jasper.EXPORT_TO = EXPORT_TO;
//            Jasper.printReports("/Relatorios/FECHAMENTO_BAILE.jasper", "fechamento_baile", list);
////            if (pdf){
////                JRPdfExporter exporter = new JRPdfExporter();
////                exporter.setExporterInput(SimpleExporterInput.getInstance(list));
////                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput("C:\\Users\\Claudemir Rtools\\Desktop\\tmp\\testeJasperExportHash.pdf"));  
////                SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
////                configuration.setCreatingBatchModeBookmarks(true);
////                exporter.setConfiguration(configuration);
////                exporter.exportReport();
////            }else{
////                JRXlsExporter exporter = new JRXlsExporter();
////                exporter.setExporterInput(SimpleExporterInput.getInstance(list));
////                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput("C:\\Users\\Claudemir Rtools\\Desktop\\tmp\\testeJasperExportHash.xls"));
////
////                SimpleXlsExporterConfiguration configuration = new SimpleXlsExporterConfiguration();
////                //configuration.setCreatingBatchModeBookmarks(true);
////                exporter.setConfiguration(configuration);
////                exporter.exportReport();
////            }
//
////            byte[] arquivo = JasperExportManager.exportReportToPdf(print);
////                
////            HttpServletResponse res = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
////            res.setContentType("application/pdf");
////            res.setHeader("Content-disposition", "inline; filename=\"" + "relatorioFechamentoBaile" + ".pdf\"");
////            res.getOutputStream().write(arquivo);
////            res.getCharacterEncoding();
////
////            FacesContext.getCurrentInstance().responseComplete();
//        } catch (JRException e) {
//            e.getMessage();
//        }
//    }
    public void imprimir() {
        RelatorioFechamentoBaileDao dao = new RelatorioFechamentoBaileDao();

        List list = dao.listaEventoBaile(Integer.valueOf(listaEventoBaile.get(idEventoBaile).getDescription()));
        List<ParametroFechamentoBaile> lista = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            List object = (List) list.get(i);
            lista.add(
                    new ParametroFechamentoBaile(
                            DataHoje.converteData((Date) object.get(0)), // EMISSAO
                            object.get(1).toString(), // OPERADOR
                            object.get(2).toString(), // CODIGO
                            object.get(3).toString(), // CONVIDADO
                            ((Integer) object.get(12) == 13 || (Integer) object.get(12) == 15) ? "Cortesia" : object.get(4).toString(), // STATUS
                            (object.get(5) != null ? object.get(5).toString() : ""), // MESA
                            (object.get(6) != null ? object.get(6).toString() : ""), // CONVITE
                            DataHoje.converteData((Date) object.get(7)), // VENCIMENTO
                            DataHoje.converteData((Date) object.get(8)), // PAGAMENTO
                            (object.get(9) != null ? ((Double) object.get(9)).floatValue() : 0), // VALOR
                            (object.get(10) != null ? object.get(10).toString() : ""), // CAIXA
                            object.get(11).toString() // OBS
                    )
            );
        }
        Jasper.TITLE = "FECHAMENTO BAILE \n " + listaEventoBaile.get(idEventoBaile).getLabel();
        Jasper.printReports("/Relatorios/FECHAMENTO_BAILE.jasper", "fechamento_baile", lista);
    }

    public void loadListaEventoBaile() {
        listaEventoBaile.clear();
        VendaBaileDao dao = new VendaBaileDao();
        List<EventoBaile> result = dao.listaBaile(mostrar_todos);
        if (!result.isEmpty()) {
            for (int i = 0; i < result.size(); i++) {
                listaEventoBaile.add(new SelectItem(
                        i,
                        result.get(i).getEvento().getDescricaoEvento().getDescricao() + " -  "
                        + result.get(i).getDataString() + " - ("
                        + result.get(i).getHoraInicio() + " às  "
                        + result.get(i).getHoraFim() + ")   "
                        + result.get(i).getQuantidadeMesas() + " mesas  / " + result.get(i).getQuantidadeConvites() + " convites",
                        Integer.toString((result.get(i)).getId())
                )
                );
            }
        }
    }

    public List<SelectItem> getListaEventoBaile() {
        return listaEventoBaile;
    }

    public Integer getIdEventoBaile() {
        return idEventoBaile;
    }

    public void setIdEventoBaile(Integer idEventoBaile) {
        this.idEventoBaile = idEventoBaile;
    }

    public Boolean getMostrar_todos() {
        return mostrar_todos;
    }

    public void setMostrar_todos(Boolean mostrar_todos) {
        this.mostrar_todos = mostrar_todos;
    }
}
